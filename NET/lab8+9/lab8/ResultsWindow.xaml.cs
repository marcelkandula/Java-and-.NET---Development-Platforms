using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace lab8
{
    public partial class ResultsWindow : Window
    {
        public ResultsWindow(string results)
        {
            InitializeComponent();
            ResultsTextBox.Text = results;
        }
    }
}
