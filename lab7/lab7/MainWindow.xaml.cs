using lab7;
using System.Collections.ObjectModel;
using System.Windows;
using System.Windows.Controls;

namespace lab7
{
    public partial class MainWindow : Window
    {
        private ObservableCollection<RecursiveItem> rootItems = new ObservableCollection<RecursiveItem>();

        public MainWindow()
        {
            InitializeComponent();
            TreeView.ItemsSource = rootItems;
        }

        private void GenerateData(object sender, RoutedEventArgs e)
        {
            rootItems.Clear();
            var root = new RecursiveItem("Root", null);
            root.Children.Add(new RecursiveItem("Child 1", root));
            root.Children.Add(new RecursiveItem("Child 2", root));
            rootItems.Add(root);
        }

        private void ShowVersion(object sender, RoutedEventArgs e)
        {
            MessageBox.Show("Version: 1.0, 06.05.2025", "Version Info");
        }

        private void Exit(object sender, RoutedEventArgs e)
        {
            Close();
        }

        private void SelectedItemChanged(object sender, RoutedPropertyChangedEventArgs<object> e)
        {
            if (e.NewValue is RecursiveItem selected)
            {
                DetailsTextBlock.Text = $"Name: {selected.Name}\nChildren: {selected.Children.Count}";
            }
        }
    }
}
