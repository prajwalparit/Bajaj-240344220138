package com.example.bajaj;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

public class DestinationHashGenerator {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar 240344220138 <path to json file>");
            System.exit(1);
        }

        String prnNumber = args[0];
        String filePath = args[1];

        String destinationValue = null;
        try {
            // Read JSON file
            JSONTokener tokener = new JSONTokener(new FileInputStream(filePath));
            Object obj = tokener.nextValue();

            if (obj instanceof JSONObject) {
                destinationValue = findDestinationValue((JSONObject) obj);
            } else if (obj instanceof JSONArray) {
                destinationValue = findDestinationValue((JSONArray) obj);
            }
            
            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                System.exit(1);
            }

            // Generate random string
            String randomString = generateRandomString(8);

            // Generate MD5 hash
            String toHash = prnNumber + destinationValue + randomString;
            String md5Hash = getMD5Hash(toHash);

            // Print result
            System.out.println(md5Hash + ";" + randomString);

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("MD5 algorithm not found.");
            e.printStackTrace();
        }
    }

    private static String findDestinationValue(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (key.equals("destination")) {
                return value.toString();
            } else if (value instanceof JSONObject) {
                String result = findDestinationValue((JSONObject) value);
                if (result != null) {
                    return result;
                }
            } else if (value instanceof JSONArray) {
                String result = findDestinationValue((JSONArray) value);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String findDestinationValue(JSONArray jsonArray) {
        for (Object item : jsonArray) {
            if (item instanceof JSONObject) {
                String result = findDestinationValue((JSONObject) item);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String getMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}