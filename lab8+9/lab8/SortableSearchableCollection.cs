using System;
using System.ComponentModel;
using System.Linq;
using System.Reflection;
using System.Collections.ObjectModel;

namespace lab9
{
    public class SortableSearchableCollection<T> : ObservableCollection<T>
    {
        /* sortowanie po dowolnej właściwości implementującej IComparable */
        public void SortBy(string propName, ListSortDirection dir = ListSortDirection.Ascending)
        {
            var prop = typeof(T).GetProperty(propName);
            if (prop is null || prop.PropertyType.GetInterface(nameof(IComparable)) is null)
                throw new InvalidOperationException("Właściwość nie implementuje IComparable");

            var ordered = dir == ListSortDirection.Ascending
                ? this.OrderBy(x => prop.GetValue(x))
                : this.OrderByDescending(x => prop.GetValue(x));

            var items = ordered.ToList();
            Clear();
            foreach (var i in items) Add(i);
        }

        /* wyszukanie elementu po String lub Int32 */
        public T? Find(string propName, object key)
        {
            var prop = typeof(T).GetProperty(propName);
            if (prop is null) return default;

            var t = prop.PropertyType;
            if (t != typeof(string) && t != typeof(int) && t != typeof(Int32)) return default;

            return this.FirstOrDefault(x => prop.GetValue(x)?.Equals(key) == true);
        }
    }
}
