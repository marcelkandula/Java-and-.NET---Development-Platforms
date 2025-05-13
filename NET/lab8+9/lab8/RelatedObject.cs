using lab8;

public class RelatedObject : IComparable<RelatedObject>
{
    public int Number1 { get; set; }
    public int Number2 { get; set; }
    public string Text { get; set; }
    public SomeEnum Option { get; set; }

    public RelatedObject()
    {
        var rnd = new Random();
        Number1 = rnd.Next(1, 10);
        Number2 = rnd.Next(1, 10);
        Text = $"Text {rnd.Next(1, 100)}";
        Option = (SomeEnum)rnd.Next(0, 3);
    }

    public int CompareTo(RelatedObject other) =>
        other is null ? 1 : Number1.CompareTo(other.Number1);
}
