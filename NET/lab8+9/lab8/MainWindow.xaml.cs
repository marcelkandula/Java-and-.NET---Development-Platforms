using lab9;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Xml.Linq;

namespace lab8
{
    public partial class MainWindow : Window
    {
        private ObservableCollection<Model> models;
        public SortableSearchableCollection<Model> Models { get; } = new();

        public MainWindow()
        {
            InitializeComponent();
            DataContext = this;
            LoadData(50);
            PropertyBox.ItemsSource = typeof(Model).GetProperties()
                     .Where(p => p.PropertyType == typeof(string) || p.PropertyType == typeof(int))
                     .Select(p => p.Name);
        }

        private void GenerateData_Click(object sender, RoutedEventArgs e)
        {
            models = GenerateData();
            TreeViewObjects.Items.Clear();
            foreach (var model in models)
            {
                TreeViewObjects.Items.Add(CreateTreeViewItem(model));
            }
        }

        private ObservableCollection<Model> GenerateData(int numberOfModels = 50)
        {
            ObservableCollection<Model> models = new ObservableCollection<Model>();

            Model rootModel = new Model("Root Model");

            Random random = new Random();

            for (int i = 0; i < numberOfModels; i++)
            {
                Model model = new Model($"Model {i + 1}");

                int numberOfChildren = random.Next(1, 4);
                for (int j = 0; j < numberOfChildren; j++)
                {
                    var childModel = new Model($"Child {i + 1} {j + 1}");

                    model.Children.Add(childModel);
                }

                rootModel.Children.Add(model);
            }

            models.Add(rootModel);
            return models;
        }

        private TreeViewItem CreateTreeViewItem(Model model)
        {
            TreeViewItem item = new TreeViewItem
            {
                Header = model.Name,
                Tag = model
            };

            foreach (var child in model.Children)
            {
                item.Items.Add(CreateTreeViewItem(child));
            }

            item.Selected += Item_Selected;
            item.ContextMenu = CreateContextMenu(item);

            return item;
        }

        private ContextMenu CreateContextMenu(TreeViewItem item)
        {
            ContextMenu contextMenu = new ContextMenu();

            MenuItem runQueryItem = new MenuItem
            {
                Header = "Run Query"
            };
            runQueryItem.Click += (sender, e) => Query((Model)item.Tag);
            contextMenu.Items.Add(runQueryItem);

            MenuItem serializeMenuItem = new MenuItem
            {
                Header = "Serialize"
            };
            serializeMenuItem.Click += (sender, e) => SerializeModel((Model)item.Tag);
            contextMenu.Items.Add(serializeMenuItem);

            MenuItem deserializeMenuItem = new MenuItem
            {
                Header = "Deserialize"
            };
            deserializeMenuItem.Click += (sender, e) => DeserializeModel(item);
            contextMenu.Items.Add(deserializeMenuItem);

            MenuItem toHTML = new MenuItem
            {
                Header = "Generate HTML"
            };
            toHTML.Click += (sender, e) => GenerateHTML((Model)item.Tag);
            contextMenu.Items.Add(toHTML);

            return contextMenu;
        }

        public void SerializeModel(Model model)
        {
            Microsoft.Win32.SaveFileDialog saveFileDialog = new Microsoft.Win32.SaveFileDialog();
            saveFileDialog.Filter = "XML Files (*.xml)|*.xml";

            if (saveFileDialog.ShowDialog() == true)
            {
                XElement modelXml = model.ToXml();
                modelXml.Save(saveFileDialog.FileName);
            }
        }

        public Model DeserializeModel(TreeViewItem selectedItem)
        {
            Microsoft.Win32.OpenFileDialog openFileDialog = new Microsoft.Win32.OpenFileDialog();
            openFileDialog.Filter = "XML Files (*.xml)|*.xml";

            if (openFileDialog.ShowDialog() == true)
            {
                XElement modelXml = XElement.Load(openFileDialog.FileName);
                Model deserializedModel = Model.FromXml(modelXml);
                Model selectedModel = (Model)selectedItem.Tag;
                selectedModel.Children.Add(deserializedModel);
                TreeViewItem deserializedItem = CreateTreeViewItem(deserializedModel);
                selectedItem.Items.Add(deserializedItem);
                return deserializedModel;
            }

            return null;
        }



        private void Item_Selected(object sender, RoutedEventArgs e)
        {
            if (sender is TreeViewItem item && item.Tag is Model model)
            {
                e.Handled = true;
                DetailsTextBlock.Text = $"ID: {model.ID}\nName: {model.Name}\n" +
                                       $"Text: {model.RelatedObject.Text}\n" +
                                       $"Enum: {model.RelatedObject.Option}\n" +
                                       $"Numeric 1: {model.RelatedObject.Number1}\n" +
                                       $"Numeric 2: {model.RelatedObject.Number2}";
            }
        }


        private void DeleteItem_Click(object sender, RoutedEventArgs e)
        {
            if (TreeViewObjects.SelectedItem is TreeViewItem selectedItem && selectedItem.Tag is Model selectedModel)
            {
                TreeViewItem parentItem = selectedItem.Parent as TreeViewItem;

                if (parentItem != null && parentItem.Tag is Model parentModel)
                {
                    parentModel.Children.Remove(selectedModel);

                    parentItem.Items.Remove(selectedItem);
                }
                else
                {
                    models.Remove(selectedModel);

                    TreeViewObjects.Items.Remove(selectedItem);
                }
            }
        }

        private void CreateItem_Click(object sender, RoutedEventArgs e)
        {
            if (TreeViewObjects.SelectedItem is TreeViewItem selectedItem && selectedItem.Tag is Model parentModel)
            {
                Model newChildModel = new Model( $"Child of {parentModel.Name}");

                parentModel.Children.Add(newChildModel);

                TreeViewItem newItem = CreateTreeViewItem(newChildModel);
                selectedItem.Items.Add(newItem);
            }
        }

        private void GenerateHTML(Model model)
        {
            if (models == null || models.Count == 0)
            {
                MessageBox.Show("No data available to generate HTML.");
                return;
            }

            Microsoft.Win32.SaveFileDialog saveFileDialog = new Microsoft.Win32.SaveFileDialog();
            saveFileDialog.Filter = "HTML Files (*.html)|*.html";
            saveFileDialog.DefaultExt = ".html";

            if (saveFileDialog.ShowDialog() == true)
            {
                HtmlGenerator g = new HtmlGenerator();
                g.GenerateHtmlFile(model, saveFileDialog.FileName);
            }
        }

        public void Query(Model selectedModel)
        {
            var results1 = from child in selectedModel.Children
                           where child.ID % 2 != 0
                           select new
                           {
                               SUM_OF = child.RelatedObject.Number1 + child.RelatedObject.Number2,
                               UPPERCASE = child.RelatedObject.Text.ToUpper()
                           };

            var results2 = results1.GroupBy(q => q.UPPERCASE)
                               .Select(g => new
                               {
                                   UPPERCASE = g.Key,
                                   AverageSum = g.Average(x => x.SUM_OF)
                               });

            StringBuilder results = new StringBuilder();
            foreach (var group in results2)
            {
                results.AppendLine($"Group: {group.UPPERCASE}, Average Sum: {group.AverageSum}");
            }

            ResultsWindow resultsWindow = new ResultsWindow(results.ToString());
            resultsWindow.Show();
        }
        private void Search_Click(object s, RoutedEventArgs e)
        {
            if (PropertyBox.SelectedItem is not string prop) return;
            object key = int.TryParse(SearchBox.Text, out int k) ? k : SearchBox.Text;
            var item = Models.Find(prop, key);
            if (item != null) GridModels.SelectedItem = item;
        }
        private void LoadData(int n)
        {
            for (int i = 0; i < n; ++i)
                Models.Add(new Model($"Model {i + 1}"));
        }
        private void Add_Click(object s, RoutedEventArgs e) =>
            Models.Add(new Model("Nowy"));

        private void Delete_Click(object s, RoutedEventArgs e)
        {
            if (GridModels.SelectedItem is Model m) Models.Remove(m);
        }
        private void GridModels_Sorting(object s, DataGridSortingEventArgs e)
        {
            e.Handled = true;
            var dir = e.Column.SortDirection != ListSortDirection.Ascending
                            ? ListSortDirection.Ascending : ListSortDirection.Descending;
            Models.SortBy(e.Column.SortMemberPath, dir);
            e.Column.SortDirection = dir;
        }

        private void Exit_Click(object sender, RoutedEventArgs e)
        {
            Application.Current.Shutdown();
        }
    }
}