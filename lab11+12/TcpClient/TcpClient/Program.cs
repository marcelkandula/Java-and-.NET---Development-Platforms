using System;
using System.IO;
using System.Net.Sockets;
using System.Runtime.Serialization.Formatters.Binary;
using System.Threading;
using SharedLib;

namespace TcpClientApp
{
    internal class Program
    {
        private const string Host = "127.0.0.1"; 
        private const int Port = 9000;

        private static void Main()
        {
            Guid selfId = Guid.NewGuid();
            var rnd = new Random();

            while (true) 
            {
                try
                {
                    using var client = new TcpClient();
                    Console.WriteLine($"[CLIENT {selfId}] Próba połączenia…");
                    client.Connect(Host, Port);
                    Console.WriteLine($"[CLIENT {selfId}] Połączono.");

                    var stream = client.GetStream();
                    var fmt = new BinaryFormatter();

                    while (client.Connected)
                    {
                        var outPkg = new DataPackage
                        {
                            Id = selfId,
                            Message = $"Hello from {selfId}",
                            Counter = rnd.Next(0, 100)
                        };

                        fmt.Serialize(stream, outPkg);            
                        var inPkg = (DataPackage)fmt.Deserialize(stream); 

                        if (inPkg.Counter != outPkg.Counter)
                        {
                            Console.WriteLine($"[CLIENT {selfId}] Counter zmieniony: {outPkg.Counter} -> {inPkg.Counter}");
                        }

                        Thread.Sleep(rnd.Next(5000, 8000));
                    }
                }
                catch (SocketException)  
                {
                    Console.WriteLine($"[CLIENT {selfId}] Brak serwera - retry za 2 s.");
                    Thread.Sleep(2000);
                }
                catch (IOException) { } 
            }
        }
    }
}
