using System;

namespace SharedLib
{
    [Serializable]
    public class DataPackage
    {
        public Guid Id { get; set; }
        public string Message { get; set; }
        public int Counter { get; set; }
    }
}
