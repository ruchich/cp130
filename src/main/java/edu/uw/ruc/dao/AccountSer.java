package edu.uw.ruc.dao;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.CreditCard;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.*;

/**
 * Created by chq-ruchic on 4/19/2017.
 */
@SuppressWarnings("serial")
public final class AccountSer implements Serializable {
    /** constant to be written to represent a null string*/
    private static final String NULL_STR = "<null>";

    /**
     * Utility class-disable constructor
     */
    private AccountSer(){

    };

    /**
     * Writes an account object to an output stream
     * @param out the output stream to write to
     * @param acct the acct object to write
     */

    public static void write(final OutputStream out, final Account acct)
        throws AccountException{
        try{
            final DataOutputStream dos = new DataOutputStream(out);
            dos.writeUTF(acct.getName());
            writeByteArray(dos, acct.getPasswordHash());
            dos.writeInt(acct.getBalance());
            writeString(dos, acct.getFullName());
            writeString(dos, acct.getPhone());
            writeString(dos, acct.getEmail());
            dos.flush();
        }catch (final IOException ex){
            throw new AccountException("Failed to write account data.", ex);
        }
                  }

    /**
     * writes the string to the output stream
      * @param out DataOutputStream to write to
     * @param s string to write
     * @throws IOException
     */

    private static void writeString(final DataOutputStream out, final String s)throws IOException{
             out.writeUTF( s==null ? NULL_STR:s);
         }

    public static Account read(final InputStream in)
            throws AccountException, IOException{
    	final DataInputStream dis = new DataInputStream(in);
    	try (ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("context.xml")) {

            final Account acct = appContext.getBean(Account.class);
            acct.setName(dis.readUTF());
            acct.setPasswordHash(readByteArray(dis));
            acct.setBalance(dis.readInt());
            acct.setFullName(readString(dis));
            acct.setPhone(readString(dis));
            acct.setEmail(readString(dis));
            dis.close();
            return acct;
    	}
    }
            
    
    /**
     * convenience methos to read from stream
     */

    private static String readString(final DataInputStream in) throws IOException{
        final String s = in.readUTF();
        return NULL_STR.equals(s) ? null : s;

    }

    /**
     * convenience method to write arrays
     */
    private static void writeByteArray( final DataOutputStream out, final byte[] b)throws IOException{
         final int len  = (b == null) ? -1 : b.length;
        out.writeInt(len);
        if(len>0) {
            out.write(b);
        }
    }
    /**
     * method to read bytes
     */
    private  static byte[] readByteArray( final DataInputStream in) throws IOException{
        byte[] bytes = null;
        final int len = in.readInt();

        if(len>=0){
            bytes = new byte[len];
            in.readFully(bytes);
        }
        return bytes;
    }
}
