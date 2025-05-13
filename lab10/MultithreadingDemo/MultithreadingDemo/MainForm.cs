using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace MultithreadingDemo
{
    public partial class MainForm : Form
    {
        ComboBox comboImpl;
        NumericUpDown numThreads;
        ProgressBar progressBar;
        Button btnStart, btnCancel;
        Label lblThreads, lblImpl;

        ConcurrentQueue<int> dataPool;
        ConcurrentBag<Result> results;
        const int totalItems = 1000;
        CancellationTokenSource cts;
        List<BackgroundWorker> bgWorkers;

        public MainForm()
        {
            InitializeComponent();
            Text = "Multithreading";
            Width = 400; Height = 200;

            lblImpl = new Label { Text = "Implementation:", Left = 10, Top = 15, Width = 100 };
            comboImpl = new ComboBox { Left = 120, Top = 10, Width = 240, DropDownStyle = ComboBoxStyle.DropDownList };
            comboImpl.Items.AddRange(new[] {
                "1) Task/Task<T>",
                "2) Delegates BeginInvoke",
                "3) async/await",
                "4) BackgroundWorker"
            });
            comboImpl.SelectedIndex = 0;

            lblThreads = new Label { Text = "Threads:", Left = 10, Top = 50, Width = 100 };
            numThreads = new NumericUpDown { Left = 120, Top = 45, Width = 60, Minimum = 1, Maximum = 16, Value = 2 };

            progressBar = new ProgressBar { Left = 10, Top = 85, Width = 350, Height = 25 };

            btnStart = new Button { Text = "Start", Left = 80, Top = 120, Width = 80 };
            btnCancel = new Button { Text = "Cancel", Left = 200, Top = 120, Width = 80, Enabled = false };

            Controls.AddRange(new Control[] { lblImpl, comboImpl, lblThreads, numThreads, progressBar, btnStart, btnCancel });

            btnStart.Click += BtnStart_Click;
            btnCancel.Click += BtnCancel_Click;
        }

        private void BtnStart_Click(object sender, EventArgs e)
        {
            btnStart.Enabled = false;
            comboImpl.Enabled = false;
            numThreads.Enabled = false;
            btnCancel.Enabled = true;

            var rand = new Random();
            dataPool = new ConcurrentQueue<int>(Enumerable.Range(0, totalItems).Select(_ => rand.Next(1000)));
            results = new ConcurrentBag<Result>();
            progressBar.Maximum = totalItems;
            progressBar.Value = 0;
            cts = new CancellationTokenSource();

            int n = (int)numThreads.Value;
            switch (comboImpl.SelectedIndex)
            {
                case 0: RunWithTasks(n); break;
                case 1: RunWithDelegates(n); break;
                case 2: RunWithAsyncAwait(n); break;
                case 3: RunWithBackgroundWorker(n); break;
            }
        }

        private void BtnCancel_Click(object sender, EventArgs e)
        {
            btnCancel.Enabled = false;
            cts.Cancel();
            if (bgWorkers != null)
                bgWorkers.ForEach(bw => bw.CancelAsync());
        }

        // 1) Task / Task<T>
        private void RunWithTasks(int n)
        {
            var sw = Stopwatch.StartNew();
            var tasks = Enumerable.Range(0, n)
                .Select(_ => Task.Run(() => WorkerLoop(cts.Token)))
                .ToArray();
            Task.WhenAll(tasks).ContinueWith(_ =>
                {
                    sw.Stop();
                    Done(sw.ElapsedMilliseconds);
                },
                TaskScheduler.FromCurrentSynchronizationContext());
        }

        private void WorkerLoop(CancellationToken token)
        {
            while (!token.IsCancellationRequested && dataPool.TryDequeue(out int item))
            {
                var start = Stopwatch.GetTimestamp();
                int outv = Compute(item);
                var end = Stopwatch.GetTimestamp();
                results.Add(new Result(item, outv, (end - start) * 1000 / Stopwatch.Frequency));
                Invoke((Action)(() => progressBar.Value++));
            }
        }
        // delegates BeginInvoke/EndInvoke
        private void RunWithDelegates(int n)
        {
            var sw = Stopwatch.StartNew();

            Action worker = () =>
            {
                while (!cts.IsCancellationRequested && dataPool.TryDequeue(out int item))
                {
                    var start = Stopwatch.GetTimestamp();
                    int outv = Compute(item);
                    var end = Stopwatch.GetTimestamp();

                    results.Add(new Result(item, outv, (end - start) * 1000 / Stopwatch.Frequency));

                    Invoke((Action)(() => progressBar.Value++));
                }
            };

            var handles = new List<IAsyncResult>(n);
            for (int i = 0; i < n; i++)
            {
                handles.Add(worker.BeginInvoke(null, null));
            }

            Task.Run(() =>
            {
                foreach (var h in handles)
                    worker.EndInvoke(h);

                sw.Stop();
                Invoke((Action)(() => Done(sw.ElapsedMilliseconds)));
            });
        }


        private Result ComputeAndCollect(int input)
        {
            var start = Stopwatch.GetTimestamp();
            int outv = Compute(input);
            var end = Stopwatch.GetTimestamp();
            var res = new Result(input, outv, (end - start) * 1000 / Stopwatch.Frequency);
            results.Add(res);
            return res;
        }

        // 3) async/await
        private async void RunWithAsyncAwait(int n)
        {
            var sw = Stopwatch.StartNew();
            var tasks = Enumerable.Range(0, n)
                .Select(_ => WorkerAsync(cts.Token))
                .ToArray();
            await Task.WhenAll(tasks);
            sw.Stop();
            Done(sw.ElapsedMilliseconds);
        }

        private async Task WorkerAsync(CancellationToken token)
        {
            while (!token.IsCancellationRequested && dataPool.TryDequeue(out int item))
            {
                var start = Stopwatch.GetTimestamp();
                int outv = await Task.Run(() => Compute(item));
                var end = Stopwatch.GetTimestamp();
                results.Add(new Result(item, outv, (end - start) * 1000 / Stopwatch.Frequency));
                Invoke((Action)(() => progressBar.Value++));
            }
        }

        // 4) BackgroundWorker
        private void RunWithBackgroundWorker(int n)
        {
            var sw = Stopwatch.StartNew();
            bgWorkers = new List<BackgroundWorker>();
            int finished = 0;

            for (int i = 0; i < n; i++)
            {
                var bw = new BackgroundWorker { WorkerReportsProgress = true, WorkerSupportsCancellation = true };
                bw.DoWork += (s, e) =>
                {
                    while (!bw.CancellationPending && dataPool.TryDequeue(out int item))
                    {
                        var start = Stopwatch.GetTimestamp();
                        int outv = Compute(item);
                        var end = Stopwatch.GetTimestamp();
                        results.Add(new Result(item, outv,
                            (end - start) * 1000 / Stopwatch.Frequency));
                        bw.ReportProgress(1);
                    }
                };
                bw.ProgressChanged += (s, e) =>
                {
                    Invoke((Action)(() => progressBar.Value++));
                };
                bw.RunWorkerCompleted += (s, e) =>
                {
                    if (Interlocked.Increment(ref finished) == n)
                    {
                        sw.Stop();
                        Invoke((Action)(() => Done(sw.ElapsedMilliseconds)));
                    }
                };
                bgWorkers.Add(bw);
                bw.RunWorkerAsync();
            }
        }





        private int Compute(int input)
        {
            long result = 1;
            int iterations = 1400 * input + 13;
            for (int i = 1; i <= iterations; i++)
            {
                result = (result * i + input) % int.MaxValue;
                result -= (int)(result / i - Math.Sqrt(result));
            }
            return (int)result;
        }

        private void Done(long ms)
        {
            MessageBox.Show($"Gotowe w {ms} ms\nWyników: {results.Count}",
                "Koniec", MessageBoxButtons.OK, MessageBoxIcon.Information);

            btnStart.Enabled = true;
            comboImpl.Enabled = true;
            numThreads.Enabled = true;
            btnCancel.Enabled = false;
        }
    }

    public class Result
    {
        public int Input { get; }
        public int Output { get; }
        public long ProcessingTimeMs { get; }
        public Result(int i, int o, long t)
        {
            Input = i; Output = o; ProcessingTimeMs = t;
        }
        public override string ToString() =>
            $"Result{{in={Input}, out={Output}, time={ProcessingTimeMs}ms}}";
    }
}
