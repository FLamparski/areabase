package lamparski.areabase.utils;
/** !license-block 
    This file is part of Areabase.

    Areabase is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Areabase is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Areabase.  If not, see <http://www.gnu.org/licenses/>.

    Areabase (C) 2013-2014 Filip Wieland <filiplamparski@gmail.com>
*/
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

/**
 * Objects to byte arrays and vice versa
 * 
 * @author filip
 * 
 */
@Deprecated
@SuppressWarnings("unused")
public class Pickle {
	/**
	 * Creates an object from the byte array <i>pickle</i>.
	 * 
	 * @param pickle
	 *            The byte array to unpack
	 * @return The object that was stored in the array
	 * @throws StreamCorruptedException
	 *             When the array provided is garbage
	 * @throws IOException
	 *             ObjectInput throws it, but since it's all memory it should
	 *             not really happen
	 * @throws ClassNotFoundException
	 *             When trying to unpack an object that's not defined in client
	 *             code
	 */
	public static Object load(byte[] pickle) throws StreamCorruptedException,
			IOException, ClassNotFoundException {
		ByteArrayInputStream instream = new ByteArrayInputStream(pickle);
		ObjectInput oin = new ObjectInputStream(instream);
		Object o = oin.readObject();
		oin.close();
		instream.close();
		return o;
	}

	/**
	 * Packs the object <i>s</i> into a byte array.
	 * 
	 * @param s
	 *            The object to pack
	 * @return A byte array ready to be, say, stored in a database as a blob
	 * @throws IOException
	 *             Streams throw it
	 */
	public static byte[] dump(Serializable s) throws IOException {
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		ObjectOutput oout = new ObjectOutputStream(outstream);
		oout.writeObject(s);
		byte[] pickle = outstream.toByteArray();
		oout.close();
		outstream.close();
		return pickle;
	}
}
