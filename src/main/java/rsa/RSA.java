package rsa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class RSA {

    private int p;
    private int q;
    private int e;
    private int[] publicKey;
    private int[] privateKey;
    private final ArrayList<Integer> encryptedMessage = new ArrayList<>();
    private final ArrayList<Integer> decryptedMessage = new ArrayList<>();

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public int getQ() {
        return q;
    }

    public void setQ(int q) {
        this.q = q;
    }

    public int getN() {
        return this.getP() * this.getQ();
    }

    public int getPhiN() {
        return (this.getP() - 1) * (this.getQ() - 1);
    }

    public int getE() {
        return e;
    }

    public void setE(int e) {
        this.e = e;
    }

    public int getD() {
        int x = 1;
        while (true) {
            int temp = this.getPhiN() * x + 1;
            if (temp % this.getE() == 0) {
                return temp / this.getE();
            }
            else {
                x++;
            }
        }
    }

    public int[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(int[] publicKey) {
        this.publicKey = publicKey;
    }

    public int[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(int[] privateKey) {
        this.privateKey = privateKey;
    }

    public ArrayList<Integer> getEncryptedMessage() {
        return encryptedMessage;
    }

    public ArrayList<Integer> getDecryptedMessage() {
        return decryptedMessage;
    }

    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public BufferedReader getReader() {
        return this.reader;
    }

    private void valueGetter(char valueOf) throws IOException {
        boolean valueIsSet = false;
        while (!valueIsSet) {
            System.out.println("Adja meg a(z) "+ valueOf +" ??rt??k??t:");
            String value = this.getReader().readLine();
            int parsedValue = 0;
            try {
                parsedValue = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.err.println("A megadott "+ valueOf +" nem egy sz??m!");
            }

            if (parsedValue != 0) {
                if (valueOf == 'p' || valueOf == 'q') {
                    switch (valueOf) {
                        case 'p' -> {
                            if (!Prime.isPrime(parsedValue)) {
                                System.err.println("A megadott "+ valueOf +" nem egy pr??m sz??m!");
                            }
                            else {
                                this.setP(parsedValue);
                                valueIsSet = true;
                            }
                        }
                        case 'q' -> {
                            if (!Prime.isPrime(parsedValue)) {
                                System.err.println("A megadott "+ valueOf +" nem egy pr??m sz??m!");
                            }
                            else {
                                this.setQ(parsedValue);
                                valueIsSet = true;
                            }
                        }
                    }
                } else if (valueOf == 'e') {
                    if (parsedValue % this.getN() == 0) {
                        System.err.println("A megadott titkos??t?? exponens oszthat?? n-nel.");
                        System.exit(-1);
                    }
                    else if (parsedValue < 0 || parsedValue > this.getPhiN()) {
                        System.err.println("A megadott titkos??t?? exponens kisebb, mint 1, vagy nagyobb, mint a phi(N)!");
                        System.exit(-1);
                    }
                    else {
                        this.setE(parsedValue);
                        valueIsSet = true;
                    }
                }
            }
        }
    }

    private void init() throws IOException {

        // Get the value of p
        this.valueGetter('p');
        // Get the value if q
        this.valueGetter('q');

        // Get the value of e
        this.valueGetter('e');

        // Set the public key
        this.setPublicKey(new int[]{this.getE(), this.getN()});

        // Set the private key
        this.setPrivateKey(new int[]{this.getD(), this.getN()});
    }

    public void encrypt(String messageToEncrypt) {
        if (this.getEncryptedMessage().size() != 0) {
            this.getEncryptedMessage().clear();
        }
        boolean isLetters = false;
        int messageOfNumbers = 0;
        try {
            messageOfNumbers = Integer.parseInt(messageToEncrypt);
        } catch (NumberFormatException exc) {
            System.out.println("A megadott ??zenet egy sz??veg, ??gy ak??ppen lesz titkos??tva!");
            isLetters = true;
        }
        if (isLetters) {
            for (var i = 0; i < messageToEncrypt.length(); i++) {
                this.getEncryptedMessage()
                        .add(FastModularExponentiation
                                .fastModularExponentiation(messageToEncrypt.charAt(i), this.getE(), this.getN()));
            }
        } else {
            this.getEncryptedMessage()
                    .add(FastModularExponentiation
                            .fastModularExponentiation(messageOfNumbers, this.getE(), this.getN()));
        }
        System.out.println("\nA titkos??tott ??zenet: " + this.getEncryptedMessage() + "\n");
    }

    public void decrypt(ArrayList<Integer> elements) throws IOException {
        for (Integer element: elements) {
            this.getDecryptedMessage()
                    .add(FastModularExponentiation
                            .fastModularExponentiation(element, this.getD(), this.getN()));
        }
        System.out.println("\nA visszafejtett ??zenet: " + this.getDecryptedMessage() + "\n");
        System.out.println("Szeretn?? a visszakapott ??zenetet ASCII karakterekk??nt l??tni?(y/n)");
        String getASCII = this.getReader().readLine();
        if (Objects.equals(getASCII, "y")) {
            StringBuilder message = new StringBuilder();
            for (Integer element : this.getDecryptedMessage()) {
                message.append((char) ((int) element));
            }
            System.out.println("\nA visszafejtett ??zenet sz??vegesen: " + message + "\n");
        }
    }

    public static void main(String[] args) throws IOException {

        RSA rsa = new RSA();

        rsa.init();

        String terminalCommand = "";
        while (!Objects.equals(terminalCommand, "q")) {
            System.out.println("""
            Mit szeretne v??grehajatani?
            t    -> Titkos??t??s
            v    -> Visszafejt??s
            pu   -> Mi a publikus kulcs?
            pr   -> Mi a priv??t kulcs?
            back -> Az el??z?? titkos??tott ??zenet visszafejt??se.
            q    -> Kil??p??s""");
            terminalCommand = rsa.getReader().readLine();
            if (Objects.equals(terminalCommand, "t")) {
                System.out.println("Adja meg a titkos??tani k??v??nt ??zenetet:");
                String message = rsa.getReader().readLine();
                rsa.encrypt(message);
            }
            else if (Objects.equals(terminalCommand, "v")) {
                System.out.println("Adja meg a visszafejteni k??v??nt sz??mok darabsz??m??t:");
                String numberOfMessageToBeDecrypted = rsa.getReader().readLine();
                int parsed = 0;
                try {
                    parsed = Integer.parseInt(numberOfMessageToBeDecrypted);
                } catch (NumberFormatException e) {
                    System.err.println("A megadott elem nem egy sz??m!");
                }
                ArrayList<Integer> elements = new ArrayList<>();
                System.out.println("Adja meg az elemeket enterrel elv??lasztva:");
                int i = 0;
                while (i != parsed) {
                    String element = rsa.getReader().readLine();
                    try {
                        elements.add(Integer.parseInt(element));
                        i++;
                    } catch (NumberFormatException e) {
                        System.err.println("A megadott elem nem egy sz??m!");
                    }
                }
                rsa.decrypt(elements);
            }
            else if (Objects.equals(terminalCommand, "pu")) {
                System.out.println("\nA publikus kulcs: " + Arrays.toString(rsa.getPublicKey()) + "\n");
            }
            else if (Objects.equals(terminalCommand, "pr")) {
                System.out.println("\nA priv??t kulcs: " + Arrays.toString(rsa.getPrivateKey()) + "\n");
            }
            else if (Objects.equals(terminalCommand, "back")) {
                if (rsa.getEncryptedMessage().size() > 0) {
                    rsa.decrypt(rsa.getEncryptedMessage());
                }
                else {
                    System.err.println("M??g egy titkos??t??s sem t??rt??nt!");
                }
            }
            else {
                System.err.println("Ez a lehet??s??g nem t??mogatott!");
            }
        }
    }

}
