import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.*;

class JRDCli {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        final long UPPERBOUND = (long) Math.pow(2, 32);
        String generator;

        //Creating a list of generators
        List<String> generators = new ArrayList<>();
        Provider[] providers = Security.getProviders();
        for (Provider provider: providers) {
            for (Provider.Service service : provider.getServices()) {
                if (service.getType().equals("SecureRandom")) {
                    generators.add(service.getAlgorithm());
                }
            }
        }
        //Creating a scanner
        Scanner scan = new Scanner(System.in);
        //Getting the data type
        System.out.print("Data type(bin/txt): ");
        String data_type = scan.nextLine();
        //Getting the file size
        if(data_type.equals("bin")){
            System.out.print("File size(MB): ");
        } else {
            System.out.print("File size(num amount): ");
        }
        String size = scan.nextLine();
        //Getting the generator
        while(true) {
            System.out.print("Generator: ");
            generator = scan.nextLine();
            if (generator.equals("h")) {
                for (int i = 1; i<generators.size()+1; i++) {
                    System.out.println(i + ". " + generators.get(i-1));
                }

            } else {
                break;
            }
        }
        //Getting the file path
        System.out.print("File Path: ");
        String loc = scan.nextLine();
        try {
            generator = generators.get(Integer.parseInt(generator)-1);
        } catch (NumberFormatException e) {

        }
        if(loc.equals("h")) {
            System.out.println("File Path: " + generator + "." + data_type);
            loc = (generator + "." + data_type);
        }
        SecureRandom rndGen = SecureRandom.getInstance(Objects.requireNonNull(generator));
        //Creating either a bin or txt file based on user choice
        if (data_type.equals("txt")) {
            //Opening or creating a file
            FileWriter myFileWriter = new FileWriter(loc);
            myFileWriter.flush();
            //Writing the header
            myFileWriter.write("#==================================================================\n" +
                    "# generator Java " + generator + "\n" +
                    "#==================================================================\n");
            myFileWriter.write("type: d\n" + "count: " + size + "\n" + "numbit: 32\n");
            //Writing the random numbers
            for (int i = 0; i < (Integer.parseInt(size)); i++) {
                myFileWriter.write(rndGen.nextLong(UPPERBOUND) + "\n");
            }
            myFileWriter.close();
        } else {
            //Opening or creating a file
            DataOutputStream myOutputStream = new DataOutputStream(new FileOutputStream(loc));
            myOutputStream.flush();
            //Generating and writing random bytes
            byte[] my_bytes = new byte[Integer.parseInt(size)*1048576];
            rndGen.nextBytes(my_bytes);
            myOutputStream.write(ByteBuffer.wrap(my_bytes).array());
            myOutputStream.close();
        }


    }
}