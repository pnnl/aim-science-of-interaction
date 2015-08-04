package gov.pnnl.aim.discovery.streams;

import gov.pnnl.aim.discovery.streams.AimUtils.CredentialsWindow.DialogType;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.log4j.Logger;

/**
 * Utilities to support the AIM workshop
 * 
 * @author d3y468 (Ryan LaMothe)
 * 
 */
public class AimUtils {

    private static final Logger LOG = Logger.getLogger(AimUtils.class);

    /**
     * This method reads user name from the either the Command-Line or the Eclipse Console window
     * 
     * @return Value typed in by the user
     * @throws IOException
     *             Exception that is thrown if Java in unable to read the user input
     */
    public static String readUsername() throws IOException {
        if (System.console() != null) {
            LOG.debug("Username: ");
            return System.console().readLine();
        } else {
            // final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            // line = bufferedReader.readLine();
            final CredentialsWindow gui = new CredentialsWindow(DialogType.USERNAME);

            while (gui.getUsername() == null) {
                try {
                    Thread.sleep(500);
                } catch (final InterruptedException e) {
                    // Ignore
                }
            }

            return gui.getUsername();
        }
    }

    /**
     * This method reads user password from the either the Command-Line or the Eclipse Console window
     * 
     * @return Value typed in by the user
     * @throws IOException
     *             Exception that is thrown if Java in unable to read the user input
     */
    public static String readPassword() throws IOException {
        if (System.console() != null) {
            LOG.debug("Password: ");
            return new String(System.console().readPassword());
        } else {
            // final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            // line = bufferedReader.readLine();
            final CredentialsWindow gui = new CredentialsWindow(DialogType.PASSWORD);

            while (gui.getPassword() == null) {
                try {
                    Thread.sleep(500);
                } catch (final InterruptedException e) {
                    // Ignore
                }
            }

            return new String(gui.getPassword());
        }
    }

    /**
     * Serializes an Avro object to the appropriate ByteBuffer for use in Kafka
     * 
     * @param record
     *            The Avro object that needs to be serialized to a ByteBuffer
     * @return ByteBuffer
     *         The specified Avro object serialized as a ByteBuffer
     * @throws IOException
     */
 	public static <T> ByteBuffer serializeAvroRecordToByteBuffer(T record) throws IOException {
			ByteBuffer serialized = null;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			final BinaryEncoder enc = new EncoderFactory().binaryEncoder(out, null);
			
			// Serialize the data
			@SuppressWarnings("unchecked")
			final SpecificDatumWriter<T> writer = (SpecificDatumWriter<T>) new SpecificDatumWriter<>(record.getClass());
			
			writer.write(record, enc);
			enc.flush();
			
			serialized = ByteBuffer.allocate(out.toByteArray().length);
			serialized.put(out.toByteArray());
		}
		
		return serialized;
 	}
    
    /**
     * De-serializes and constructs user-specified Avro return type from byte array
     * 
     * @param <T>
     *            Type to be processed
     * @param bytes
     *            Raw Avro bytes either pulled from Kafka
     * @return T
     *         Type to be returned
     * @throws IOException
     */
    public static <T> T deserializeAvroRecordFromByteArray(byte[] bytes, Class<T> clazz) throws IOException {
        final SpecificDatumReader<T> reader = new SpecificDatumReader(clazz);
        final BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
        return reader.read(null, decoder);
    }

    /**
     * This inner class is used for a username/password GUI to be used with a System Console in unavailable
     * 
     * @author d3y468 (Ryan LaMothe)
     * 
     */
    static class CredentialsWindow {

        public static enum DialogType {
            USERNAME, PASSWORD
        };

        private final JFrame frame = new JFrame("Credentials");

        private final JPanel panel = new JPanel();

        private final JLabel usernameLabel = new JLabel("Username");

        private final JLabel passwordLabel = new JLabel("Password");

        private final JTextField usernameTextField = new JTextField(20);

        private final JPasswordField passwordTextField = new JPasswordField(20);

        private final JButton submitButton = new JButton("Submit");

        private String username;

        private char[] password;

        public CredentialsWindow(DialogType dialogType) {
            frame.add(panel, BorderLayout.CENTER);

            switch (dialogType) {
                case USERNAME:
                    panel.add(usernameLabel);
                    panel.add(usernameTextField);
                    break;
                case PASSWORD:
                    panel.add(passwordLabel);
                    panel.add(passwordTextField);
                    break;
            }

            panel.add(submitButton);

            final Toolkit tk = Toolkit.getDefaultToolkit();
            final int frame_width = ((int) tk.getScreenSize().getWidth());
            final int frame_height = ((int) tk.getScreenSize().getHeight());

            submitButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    username = usernameTextField.getText();
                    password = passwordTextField.getPassword();

                    // Clean-up
                    frame.setVisible(false);
                    frame.dispose();
                }
            });

            frame.setLocation((frame_width / 2), (frame_height / 2));
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getRootPane().setDefaultButton(submitButton);
            frame.pack();
            frame.setVisible(true);
        }

        /**
         * @return the username
         */
        public String getUsername() {
            return username;
        }

        /**
         * @return the password
         */
        public char[] getPassword() {
            return password;
        }
    }
}
