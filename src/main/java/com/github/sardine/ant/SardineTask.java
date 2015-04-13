package com.github.sardine.ant;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.github.sardine.ant.command.Copy;
import com.github.sardine.ant.command.CreateDirectory;
import com.github.sardine.ant.command.Delete;
import com.github.sardine.ant.command.Exists;
import com.github.sardine.ant.command.Move;
import com.github.sardine.ant.command.Put;

/**
 * Controller for the Sardine ant Task
 *
 * @author jonstevens
 */
public class SardineTask extends Task
{
	/** Commands. */
	private List<Command> fCommands = new ArrayList<Command>();

	/** Attribute failOnError. */
	private boolean fFailOnError = false;

	/** Attribute username. */
	private String fUsername = null;

	/** Attribute password. */
	private String fPassword = null;

	/** Attribute domain for NTLM authentication. */
	private String fDomain = null;

	/** Attribute workstation for NTLM authentication. */
	private String fWorkstation = null;

	/** Attribute ignoreCookies. */
	private boolean fIgnoreCookies = false;

	/** Attribute preemptiveAuthenticationHost. */
	private String fPreemptiveAuthenticationHost;

	/** Reference to sardine impl. */
	private Sardine fSardine = null;

	/** Add a copy command. */
	public void addCopy(Copy copy) {
		addCommand(copy);
	}

	/** Add a createDirectory command. */
	public void addCreateDirectory(CreateDirectory createDirectory) {
		addCommand(createDirectory);
	}

	/** Add a delete command. */
	public void addDelete(Delete delete) {
		addCommand(delete);
	}

	/** Add a delete command. */
	public void addExists(Exists exists) {
		addCommand(exists);
	}

	/** Add a move command. */
	public void addMove(Move move) {
		addCommand(move);
	}

	/** Add a put command. */
	public void addPut(Put put) {
		addCommand(put);
	}

	/** Internal addCommand implementation. */
	private void addCommand(Command command) {
		command.setTask(this);
		fCommands.add(command);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() throws BuildException {
		try {
			if ( fDomain==null && fWorkstation==null ) {
				fSardine = SardineFactory.begin(fUsername, fPassword);
			} else {
				fSardine = SardineFactory.begin();
				fSardine.setCredentials(fUsername, fPassword, fDomain, fWorkstation);
			}

			if (fIgnoreCookies) {
				fSardine.ignoreCookies();
			}

			if (fPreemptiveAuthenticationHost != null && !fPreemptiveAuthenticationHost.isEmpty()) {
				fSardine.enablePreemptiveAuthentication(fPreemptiveAuthenticationHost);
			}

			for (Command command: fCommands) {
				command.executeCommand();
			}
		} catch (Exception e) {
			throw new BuildException("failed: " + e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Set the fail on error behavior.
	 *
	 * @param failonerror <code>true</code> to fail on the first error; <code>false</code> to just log errors
	 *        and continue
	 */
	public void setFailonerror(boolean failonerror) {
		fFailOnError = failonerror;
	}

	/**
	 * Returns the fail on error behavior.
	 *
	 * @return <code>true</code> to fail on the first error; <code>false</code> to just log errors and
	 *         continue
	 */
	public boolean isFailonerror() {
		return fFailOnError;
	}

	/**
	 * Setter for attribute username.
	 *
	 * @param username used for authentication
	 */
	public void setUsername(String username) {
		fUsername = username;
	}

	/**
	 * Setter for attribute password.
	 *
	 * @param password used for authentication
	 */
	public void setPassword(String password) {
		fPassword = password;
	}
	
	/**
	 * Setter for attribute domain for NTLM authentication.
	 *
	 * @param domain used for NTLM authentication
	 */
	public void setDomain(String domain) {
		fDomain = domain;
	}

	/**
	 * Setter for attribute workstation for NTLM authentication.
	 *
	 * @param workstation used for NTLM authentication
	 */
	public void setWorkstation(String workstation) {
		fWorkstation = workstation;
	}

	/**
	 * Setter for attribute ignoreCookies.
	 *
	 * @param Whether to ignore cookies.
	 */
	public void setIgnoreCookies(boolean ignoreCookies) {
		fIgnoreCookies = ignoreCookies;
	}

	/**
	 * Setter for attribute preemptiveAuthenticationHost.
	 *
	 * @param host name of the host to acivate the preemptive authentication for
	 */
	public void setPreemptiveAuthenticationHost(String host) {
		fPreemptiveAuthenticationHost = host;
	}

	/**
	 * Returns the sardine impl.
	 *
	 * @return the sardine impl
	 */
	public Sardine getSardine() {
		return fSardine;
	}
}
