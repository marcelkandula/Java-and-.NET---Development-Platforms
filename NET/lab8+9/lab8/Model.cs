using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace lab8
{
    public class Model
    {
        public int ID { get; set; }
        public string Name { get; set; }
        public RelatedObject RelatedObject { get; set; }
        public ObservableCollection<Model> Children { get; set; }

        public Model(string name)
        {
            Random random = new Random();
            ID = random.Next();
            Name = name;
            RelatedObject = new RelatedObject();
            Children = new ObservableCollection<Model>();
        }
        public XElement ToXml()
        {
            return new XElement("Model",
                new XAttribute("ID", ID),
                new XAttribute("Name", Name),
                new XElement("RelatedObject",
                    new XElement("Number1", RelatedObject.Number1),
                    new XElement("Number2", RelatedObject.Number2),
                    new XElement("Text", RelatedObject.Text),
                    new XElement("Option", RelatedObject.Option)),
                new XElement("Children", Children.Select(c => c.ToXml()))
            );
        }

        public static Model FromXml(XElement element)
        {
            var nameAttribute = element.Attribute("Name");

            if (nameAttribute == null)
            {
                throw new ArgumentNullException("Required attribute missing from the XML.");
            }

            string name = nameAttribute.Value;

            Model model = new Model(name);

            var RelatedElement = from el in element.Elements()
                                 where el.Name == "RelatedObject"
                                 select el;

            foreach (var p in RelatedElement.Elements())
            {
                if (p.Name == "Number1") { model.RelatedObject.Number1 = int.Parse(p.Value); }
                else if (p.Name == "Number2") { model.RelatedObject.Number2 = int.Parse(p.Value); }
                else if (p.Name == "Text") { model.RelatedObject.Text = p.Value; }
                else if (p.Name == "Option") { model.RelatedObject.Option = (SomeEnum) Enum.Parse(typeof(SomeEnum), p.Value, true); }
            }

            var childrenElements = from el in element.Elements()
                                   where el.Name == "Children"
                                   select el;

            foreach (var childElement in childrenElements.Elements())
            {
                Console.WriteLine(childElement.Name);
                Model childModel = FromXml(childElement);
                model.Children.Add(childModel);
            }

            return model;
        }

    }


    public enum SomeEnum
    {
        Option1,
        Option2,
        Option3
    }

}
