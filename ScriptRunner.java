import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ScriptRunner extends Thread 
{

	private final String script;
	private final StringBuilder res;
	public int exitcode = -1;
	private Process exec;
	
	public ScriptRunner(String script, StringBuilder res)
	{		
		this.script = script;
		this.res = res;		
	}
	
	@Override
	public void run() 
	{
	
		try {
						
			exec = Runtime.getRuntime().exec("su");
			final OutputStreamWriter out = new OutputStreamWriter(exec.getOutputStream());
			
			out.write(script);
			
			if (!script.endsWith("\n")) out.write("\n");
			out.flush();
			
			out.write("exit\n");
			out.flush();
			final char buf[] = new char[1024];
			
			InputStreamReader r = new InputStreamReader(exec.getInputStream());
			int read=0;
			while ((read=r.read(buf)) != -1) 
				if (res != null) res.append(buf, 0, read);
			
			
			r = new InputStreamReader(exec.getErrorStream());
			read=0;
			
			while ((read=r.read(buf)) != -1) 
				if (res != null) res.append(buf, 0, read);
			
			
			if (exec != null) this.exitcode = exec.waitFor();
			
		} catch (InterruptedException ex) {
			if (res != null) res.append("\nFucked");
		} catch (Exception ex) {
			if (res != null) res.append("\n" + ex);
		} finally {
			destroy();
		}
		
	}

	public synchronized void destroy() {
		if (exec != null) exec.destroy();
		exec = null;
	}
	
}


