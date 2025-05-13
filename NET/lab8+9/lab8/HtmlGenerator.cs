using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Xml.Linq;

namespace lab8
{
    public class HtmlGenerator
    {
        public static XElement ToHtmlTable(Model model)
        {
            var table = new XElement("table",
                new XElement("thead",
                    new XElement("tr",
                        new XElement("th", "ID"),
                        new XElement("th", "Name"),
                        new XElement("th", "NumericField1"),
                        new XElement("th", "NumericField2"),
                        new XElement("th", "TextField"),
                        new XElement("th", "EnumField")
                    )
                ),
                new XElement("tbody",
                    GenerateTableRows(model)
                )
            );

            return table;
        }

        private static IEnumerable<XElement> GenerateTableRows(Model model)
        {
            var row = new XElement("tr",
                new XElement("td", model.ID),
                new XElement("td", model.Name),
                new XElement("td", model.RelatedObject.Number1),
                new XElement("td", model.RelatedObject.Number2),
                new XElement("td", model.RelatedObject.Text),
                new XElement("td", model.RelatedObject.Option)
            );

            var rows = new List<XElement> { row };
            foreach (var child in model.Children)
            {
                rows.AddRange(GenerateTableRows(child));
            }

            return rows;
        }

        public void GenerateHtmlFile(Model model, string filePath)
        {
            XElement table = ToHtmlTable(model);

            XElement html = new XElement("html",
                new XElement("head",
                    new XElement("title", "Model Table")
                ),
                new XElement("body",
                    new XElement("h1", "Model Table"),
                    table
                )
            );

            File.WriteAllText(filePath, html.ToString());
        }
    }
}
