using System;
using System.Diagnostics;
using System.IO;
using System.Threading;
using System.Windows.Forms;

namespace JoeMarketLauncher
{
    static class Program
    {
        [STAThread]
        static void Main()
        {
            Process serverProcess = null;
            Process clientProcess = null;
            try
            {
                string appDir = AppDomain.CurrentDomain.BaseDirectory;
                string jarPath = Path.Combine(appDir, "JoeMarket-1.0.jar");

                if (!File.Exists(jarPath))
                {
                    MessageBox.Show("Could not find JoeMarket-1.0.jar in:\n" + appDir, "Joe Market - Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }

                // Look for bundled JRE first (64-bit first, then 32-bit)
                string javaExe = "javaw.exe";
                string jre64Path = Path.Combine(appDir, "jre64", "bin", "javaw.exe");
                string jre32Path = Path.Combine(appDir, "jre32", "bin", "javaw.exe");

                if (File.Exists(jre64Path))
                {
                    javaExe = jre64Path;
                }
                else if (File.Exists(jre32Path))
                {
                    javaExe = jre32Path;
                }

                // 1. Start the Server Process silently in the background
                ProcessStartInfo serverPsi = new ProcessStartInfo();
                serverPsi.FileName = javaExe;
                // Running server: java -cp "JoeMarket-1.0.jar" server.GroceryServer
                serverPsi.Arguments = "-cp \"" + jarPath + "\" server.GroceryServer";
                serverPsi.WorkingDirectory = appDir;
                serverPsi.UseShellExecute = false;
                serverPsi.CreateNoWindow = true;
                serverPsi.WindowStyle = ProcessWindowStyle.Hidden;

                serverProcess = Process.Start(serverPsi);

                // Wait 400ms for the server socket to bind successfully
                Thread.Sleep(400);

                // 2. Start the Client GUI Process
                ProcessStartInfo clientPsi = new ProcessStartInfo();
                clientPsi.FileName = javaExe;
                clientPsi.Arguments = "-jar \"" + jarPath + "\"";
                clientPsi.WorkingDirectory = appDir;
                clientPsi.UseShellExecute = false;
                clientPsi.CreateNoWindow = true;
                clientPsi.WindowStyle = ProcessWindowStyle.Hidden;

                clientProcess = Process.Start(clientPsi);

                // 3. Keep launcher active while the user is using the GUI
                if (clientProcess != null)
                {
                    clientProcess.WaitForExit();
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Failed to launch Joe Market:\n" + ex.Message, "Joe Market - Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            finally
            {
                // 4. Auto-kill the server process when the GUI client is closed
                try
                {
                    if (serverProcess != null && !serverProcess.HasExited)
                    {
                        serverProcess.Kill();
                    }
                }
                catch
                {
                    // Ignore exit cleanup exceptions
                }
            }
        }
    }
}
