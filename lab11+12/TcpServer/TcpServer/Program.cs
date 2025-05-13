using System;
using System.Collections.Concurrent;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Runtime.Serialization.Formatters.Binary;
using System.Threading;
using SharedLib;

namespace TcpServer
{
    internal class Program
    {
        private const int Port = 9000;     
        private const int MaxClients = 1;        // N – limit równoczesnych połączeń
        private static readonly SemaphoreSlim Slots = new(MaxClients);
        private static readonly ConcurrentDictionary<int, TcpClient> Clients = new();

        private static void Main()
        {
            var listener = new TcpListener(IPAddress.Any, Port);
            listener.Start();
            Console.WriteLine($"[SERVER] Nasłuchiwanie na {Port}, limit {MaxClients} klientów.");

            while (true)
            {
                Slots.Wait();                             
                var tcpClient = listener.AcceptTcpClient();   
                int id = tcpClient.Client.RemoteEndPoint!.GetHashCode();
                Clients[id] = tcpClient;
                Console.WriteLine($"[SERVER] + {tcpClient.Client.RemoteEndPoint}");

                ThreadPool.QueueUserWorkItem(HandleClient, id);  
            }
        }

        private static void HandleClient(object? state)
        {
            int id = (int)state!;
            if (!Clients.TryGetValue(id, out var tcp)) return;

            using (tcp)
            using (var stream = tcp.GetStream())
            {
                var fmt = new BinaryFormatter();
                try
                {
                    while (true)
                    {
                        var pkg = (DataPackage)fmt.Deserialize(stream);

                        pkg.Counter++;

                        fmt.Serialize(stream, pkg);
                    }
                }
                catch (IOException) { }          // klient zamknął połączenie
                catch (SocketException) { }     // błąd socketu
                finally
                {
                    Console.WriteLine($"[SERVER] - {tcp.Client.RemoteEndPoint}");
                    Clients.TryRemove(id, out _);
                    Slots.Release();             
                }
            }
        }
    }
}
