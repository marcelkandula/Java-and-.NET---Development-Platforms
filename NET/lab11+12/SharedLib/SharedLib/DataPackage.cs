using System;

namespace SharedLib
{
    [Serializable]
    public class DataPackage
    {
        public int Id { get; set; }
        public string Message { get; set; }
        public int Counter { get; set; }
    }
}
