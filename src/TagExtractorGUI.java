import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class TagExtractorGUI extends JFrame
{
    private JButton btnTextFile;
    private JButton btnStopWords;
    private JButton btnExtract;
    private JButton btnSave;

    private JLabel lblFileName;

    private JTextArea txtOutput;

    private File textFile;
    private File stopWordFile;

    private Map<String, Integer> tags;

    public TagExtractorGUI()
    {
        setTitle("Tag Extractor");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildGUI();

        setVisible(true);
    }

    private void buildGUI()
    {
        btnTextFile = new JButton("Select Text File");
        btnStopWords = new JButton("Select Stop Word File");
        btnExtract = new JButton("Extract Tags");
        btnSave = new JButton("Save Results");

        lblFileName = new JLabel("No file selected");

        txtOutput = new JTextArea();
        txtOutput.setEditable(false);

        JScrollPane scrollPane =
                new JScrollPane(txtOutput);

        JPanel topPanel = new JPanel();

        topPanel.add(btnTextFile);
        topPanel.add(btnStopWords);
        topPanel.add(btnExtract);
        topPanel.add(btnSave);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(lblFileName, BorderLayout.SOUTH);

        btnTextFile.addActionListener(e -> chooseTextFile());
        btnStopWords.addActionListener(e -> chooseStopWordFile());
        btnExtract.addActionListener(e -> extractTags());
        btnSave.addActionListener(e -> saveResults());
    }

    private void chooseTextFile()
    {
        JFileChooser chooser = new JFileChooser();

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION)
        {
            textFile = chooser.getSelectedFile();

            lblFileName.setText(
                    "Selected File: "
                            + textFile.getName());
        }
    }

    private void chooseStopWordFile()
    {
        JFileChooser chooser = new JFileChooser();

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION)
        {
            stopWordFile =
                    chooser.getSelectedFile();
        }
    }

    private void extractTags()
    {
        if (textFile == null || stopWordFile == null)
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select both a text file and a stop word file.");

            return;
        }

        try
        {
            Set<String> stopWords =
                    loadStopWords();

            tags = processTextFile(stopWords);

            displayTags();
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage());
        }
    }

    private Set<String> loadStopWords()
            throws IOException
    {
        Set<String> stopWords =
                new TreeSet<>();

        Scanner input =
                new Scanner(stopWordFile);

        while (input.hasNextLine())
        {
            String word =
                    input.nextLine()
                            .trim()
                            .toLowerCase();

            if (!word.isEmpty())
            {
                stopWords.add(word);
            }
        }

        input.close();

        return stopWords;
    }

    private Map<String, Integer> processTextFile(
            Set<String> stopWords)
            throws IOException
    {
        Map<String, Integer> frequencyMap =
                new TreeMap<>();

        Scanner input =
                new Scanner(textFile);

        while (input.hasNext())
        {
            String word = input.next();

            word = word.replaceAll(
                    "[^a-zA-Z]",
                    "");

            word = word.toLowerCase();

            if (word.isEmpty())
            {
                continue;
            }

            if (stopWords.contains(word))
            {
                continue;
            }

            frequencyMap.put(
                    word,
                    frequencyMap.getOrDefault(
                            word,
                            0) + 1);
        }

        input.close();

        return frequencyMap;
    }

    private void displayTags()
    {
        txtOutput.setText("");

        for (String word : tags.keySet())
        {
            txtOutput.append(
                    String.format(
                            "%-25s %d%n",
                            word,
                            tags.get(word)));
        }
    }

    private void saveResults()
    {
        if (tags == null)
        {
            JOptionPane.showMessageDialog(
                    this,
                    "No results available to save.");

            return;
        }

        JFileChooser chooser =
                new JFileChooser();

        int result =
                chooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                PrintWriter out =
                        new PrintWriter(
                                chooser.getSelectedFile());

                out.print(txtOutput.getText());

                out.close();

                JOptionPane.showMessageDialog(
                        this,
                        "Results saved successfully.");
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage());
            }
        }
    }
}