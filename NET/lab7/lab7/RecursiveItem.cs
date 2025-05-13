using RecursiveObjectViewer;
using System.Collections.ObjectModel;
using System.Windows.Controls;
using System.Windows;

namespace lab7
{
    public class RecursiveItem : TreeViewItem
    {
        public string Name { get; set; }
        public ObservableCollection<RecursiveItem> Children { get; set; }
        public RecursiveItem Parent { get; set; }
        public RecursiveItem(string name, RecursiveItem parent)
        {
            Name = name;
            Children = new ObservableCollection<RecursiveItem>();
            Header = Name;
            ItemsSource = Children;
            ContextMenu = BuildContextMenu();
            Parent = parent;
        }

        private ContextMenu BuildContextMenu()
        {
            var menu = new ContextMenu();

            var createItem = new MenuItem { Header = "Create" };
            createItem.Click += (s, e) => CreateChild();

            var deleteItem = new MenuItem { Header = "Delete" };
            deleteItem.Click += (s, e) => DeleteSelf();

            menu.Items.Add(createItem);
            menu.Items.Add(deleteItem);
            return menu;
        }

        private void CreateChild()
        {
            var dialog = new CreateItemWindow { Owner = Application.Current.MainWindow };
            if (dialog.ShowDialog() == true)
            {
                var child = new RecursiveItem(dialog.ItemName, this);
                Children.Add(child);
            }
        }
        public void DeleteSelf()
        {
            if (Parent != null)
            {
                Parent.Children.Remove(this);
            }
            Parent = null;
        }
    }
}
