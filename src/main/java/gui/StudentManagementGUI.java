package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.ClassRoom;
import model.Student;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class StudentManagementGUI extends JFrame {
    private List<ClassRoom> classRooms = new ArrayList<>();
    private static final String FILE_NAME = "classes.txt";
    private String currentFileName = FILE_NAME; // Track the current file name for loading/saving
    private JButton addBtn, updateBtn, deleteBtn, addClassBtn, removeClassBtn, applyWeightBtn;
    private JComboBox<String> classComboBox;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField idField, nameField, attendanceField, assignmentField, finalProjectField;
    private JLabel weightLabel1, weightLabel2, percent1, percent2;
    private int currentClassIndex = -1;
    private JTextField weightField1, weightField2;
    private int userId;
    public StudentManagementGUI(int userId) {
        this.userId = userId;
        setTitle("Student Score Management");
        setSize(950, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        // Load dữ liệu từ server ở background để không đơ giao diện
        new Thread(() -> {
            loadDataFromAPI();
        }).start();
    }

    private void initComponents() {
        System.out.println("Init GUI...");
        // Set font for the entire GUI
        setUIFont(new javax.swing.plaf.FontUIResource("Arial", Font.PLAIN, 14));

        // Initialize GUI components before adding to panel
        addBtn = new JButton("Add Student");
        addBtn.addActionListener(e -> addStudent());
        deleteBtn = new JButton("Delete Student");
        addClassBtn = new JButton("Add Class");
        removeClassBtn = new JButton("Remove Class");
        applyWeightBtn = new JButton("Apply");
        classComboBox = new JComboBox<>();
        idField = new JTextField(6);
        nameField = new JTextField(10);
        attendanceField = new JTextField(8);
        assignmentField = new JTextField(8);
        finalProjectField = new JTextField(5);
        weightLabel1 = new JLabel("% Personal Score:");
        weightField1 = new JTextField("50", 3);
        percent1 = new JLabel("%");
        weightLabel2 = new JLabel("Final:");
        weightField2 = new JTextField("50", 3);
        percent2 = new JLabel("%");

        // Class selection panel
        JPanel classPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        classPanel.add(new JLabel("Select Class:"));
        classPanel.add(classComboBox);
        classPanel.add(addClassBtn);
        classPanel.add(removeClassBtn);
        classPanel.add(weightLabel1);
        classPanel.add(weightField1);
        classPanel.add(percent1);
        classPanel.add(weightLabel2);
        classPanel.add(weightField2);
        classPanel.add(percent2);
        classPanel.add(applyWeightBtn);

        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Attendance:"));
        inputPanel.add(attendanceField);
        inputPanel.add(new JLabel("Lab Assignment:"));
        inputPanel.add(assignmentField);
        inputPanel.add(new JLabel("Final:"));
        inputPanel.add(finalProjectField);
        inputPanel.add(addBtn);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(18);
        JButton searchBtn = new JButton("Search");
        JButton showAllBtn = new JButton("Show All");
        searchPanel.add(new JLabel("Search Student (ID or Name):"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(showAllBtn);

        // Combine panels into topPanel (vertical)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(classPanel);
        topPanel.add(inputPanel);
        topPanel.add(searchPanel);

        // Add delete member button below delete student
        JPanel deleteMemberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton deleteMemberBtn = new JButton("Delete Student");
        deleteMemberPanel.add(deleteMemberBtn);

        // Add Attendance % and Assignment % fields and Set Weights button
        JLabel attWeightLabel = new JLabel("Attendance %:");
        JTextField attWeightField = new JTextField("50", 3);
        JLabel assignWeightLabel = new JLabel("Assignment %:");
        JTextField assignWeightField = new JTextField("50", 3);
        JButton setWeightsBtn = new JButton("Set Weights");
        deleteMemberPanel.add(attWeightLabel);
        deleteMemberPanel.add(attWeightField);
        deleteMemberPanel.add(assignWeightLabel);
        deleteMemberPanel.add(assignWeightField);
        deleteMemberPanel.add(setWeightsBtn);
        topPanel.add(deleteMemberPanel);

        setWeightsBtn.addActionListener(e -> {
            if (currentClassIndex < 0 || currentClassIndex >= classRooms.size()) return;
            try {
                int attW = Integer.parseInt(attWeightField.getText().trim());
                int assignW = Integer.parseInt(assignWeightField.getText().trim());
                if (attW + assignW != 100) {
                    JOptionPane.showMessageDialog(this, "Attendance % + Assignment % must be 100!");
                    return;
                }
                ClassRoom c = classRooms.get(currentClassIndex);
                c.setAttendanceWeight(attW / 100.0);
                c.setAssignmentWeight(assignW / 100.0);
                refreshTable();
                // saveToFile(); // Đã loại bỏ tự động lưu khi chỉnh sửa weight
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter integer values for weights!");
            }
        });

        // Khi đổi lớp, cập nhật lại 2 ô này
        classComboBox.addActionListener(e -> {
            currentClassIndex = classComboBox.getSelectedIndex();
            if (currentClassIndex >= 0 && currentClassIndex < classRooms.size()) {
                ClassRoom c = classRooms.get(currentClassIndex);
                weightField1.setText(String.valueOf((int)(c.getAttendanceAssignmentWeight() * 100)));
                weightField2.setText(String.valueOf((int)(c.getFinalProjectWeight() * 100)));
                attWeightField.setText(String.valueOf((int)(c.getAttendanceWeight() * 100)));
                assignWeightField.setText(String.valueOf((int)(c.getAssignmentWeight() * 100)));
            }
            refreshTable();
        });

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Attendance", "Assignment", "Personal Score", "Final", "Final Grade (10)", "Letter Grade (4)"}, 0) {
            public boolean isCellEditable(int row, int column) {
                // Only allow editing score columns
                return column == 2 || column == 3 || column == 5;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Set font for table and header
        Font tableFont = new Font("Segoe UI", Font.PLAIN, 14);
        table.setFont(tableFont);
        table.setRowHeight(22);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        ((javax.swing.table.DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setFont(new Font("Segoe UI", Font.BOLD, 14));
        javax.swing.table.DefaultTableCellRenderer renderer = new javax.swing.table.DefaultTableCellRenderer();
        renderer.setFont(tableFont);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Bottom panel for save/load
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton loadBtn = new JButton("Load File");
        loadBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                classRooms = util.FileHandler.readClassesFromFile(filePath);
                classComboBox.removeAllItems();
                for (model.ClassRoom c : classRooms) {
                    classComboBox.addItem(classDisplayString(c));
                }
                if (!classRooms.isEmpty()) {
                    classComboBox.setSelectedIndex(0);
                    currentClassIndex = 0;
                } else {
                    currentClassIndex = -1;
                    tableModel.setRowCount(0);
                }
                refreshTable();
                JOptionPane.showMessageDialog(this, "Loaded from file: " + filePath);
            }
        });
        bottomPanel.add(loadBtn);

        // Loại bỏ nút Load Data
        // JButton loadDataBtn = new JButton("Load Data");
        // loadDataBtn.addActionListener(e -> loadDataFromAPI());
        // bottomPanel.add(loadDataBtn);

        // Nút Save Data vẫn giữ nguyên
        JButton saveDataBtn = new JButton("Save Data");
        saveDataBtn.addActionListener(e -> saveDataToAPI());
        bottomPanel.add(saveDataBtn);

        // Listeners
        addClassBtn.addActionListener(e -> addClass());
        removeClassBtn.addActionListener(e -> removeClass());
        classComboBox.addActionListener(e -> {
            currentClassIndex = classComboBox.getSelectedIndex();
            if (currentClassIndex >= 0 && currentClassIndex < classRooms.size()) {
                ClassRoom c = classRooms.get(currentClassIndex);
                weightField1.setText(String.valueOf((int)(c.getAttendanceAssignmentWeight() * 100)));
                weightField2.setText(String.valueOf((int)(c.getFinalProjectWeight() * 100)));
            }
            refreshTable();
        });

        // When changing weights, only check when Apply is pressed
        applyWeightBtn.addActionListener(e -> {
            if (currentClassIndex < 0 || currentClassIndex >= classRooms.size()) return;
            try {
                int w1 = Integer.parseInt(weightField1.getText().trim());
                int w2 = Integer.parseInt(weightField2.getText().trim());
                if (w1 + w2 != 100) {
                    JOptionPane.showMessageDialog(this, "Total weight must be 100%!");
                    return;
                }
                ClassRoom c = classRooms.get(currentClassIndex);
                c.setAttendanceAssignmentWeight(w1 / 100.0);
                c.setFinalProjectWeight(w2 / 100.0);
                refreshTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter integer values for weights!");
            }
        });

        // Auto-save when editing scores in the table
        table.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                // Columns: 2 = attendance, 3 = assignment, 5 = final project
                if (row >= 0 && (col == 2 || col == 3 || col == 5)) {
                    String id = tableModel.getValueAt(row, 0).toString();
                    ClassRoom c = classRooms.get(currentClassIndex);
                    Student s = c.findStudentById(id);
                    if (s == null) return;
                    try {
                        List<Double> attendance = parseDoubleList(tableModel.getValueAt(row, 2).toString());
                        List<Double> assignments = parseDoubleList(tableModel.getValueAt(row, 3).toString());
                        double finalProject = parseDouble(tableModel.getValueAt(row, 5).toString());
                        if (!validateScores(attendance) || !validateScores(assignments) || !validateScore(finalProject)) {
                            throw new IllegalArgumentException();
                        }
                        s.setAttendanceScores(attendance);
                        s.setAssignmentScores(assignments);
                        s.setFinalProjectScore(finalProject);
                        // Update personal score, final grade, and letter grade
                        tableModel.setValueAt(String.format("%.2f", s.getPersonalScore(c)), row, 4);
                        tableModel.setValueAt(String.format("%.2f", s.getFinalGrade(c)), row, 6);
                        tableModel.setValueAt(s.getFinalGradeLetter(c), row, 7);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Please enter valid scores (numbers between 0 and 10, or a list separated by |)");
                        tableModel.setValueAt(joinDoubleList(s.getAttendanceScores()), row, 2);
                        tableModel.setValueAt(joinDoubleList(s.getAssignmentScores()), row, 3);
                        tableModel.setValueAt(s.getFinalProjectScore(), row, 5);
                    }
                    // saveToFile(); // Đã loại bỏ tự động lưu khi sửa điểm
                }
            }
        });
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    idField.setText(tableModel.getValueAt(row, 0).toString());
                    nameField.setText(tableModel.getValueAt(row, 1).toString());
                    attendanceField.setText(tableModel.getValueAt(row, 2).toString());
                    assignmentField.setText(tableModel.getValueAt(row, 3).toString());
                    finalProjectField.setText(tableModel.getValueAt(row, 5).toString());
                }
            }
        });

        // Layout
        setLayout(new BorderLayout(10, 10));
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Search functionality
        searchBtn.addActionListener(e -> {
            if (currentClassIndex < 0 || currentClassIndex >= classRooms.size()) return;
            String keyword = searchField.getText().trim().toLowerCase();
            if (keyword.isEmpty()) return;
            ClassRoom c = classRooms.get(currentClassIndex);
            tableModel.setRowCount(0);
            for (Student s : c.getStudents()) {
                if (s.getId().toLowerCase().contains(keyword) || s.getName().toLowerCase().contains(keyword)) {
                    tableModel.addRow(new Object[]{
                        s.getId(),
                        s.getName(),
                        joinDoubleList(s.getAttendanceScores()),
                        joinDoubleList(s.getAssignmentScores()),
                        String.format("%.2f", s.getPersonalScore(c)),
                        s.getFinalProjectScore(),
                        String.format("%.2f", s.getFinalGrade(c)),
                        s.getFinalGradeLetter(c)
                    });
                }
            }
        });
        showAllBtn.addActionListener(e -> refreshTable());

        // Delete member event
        deleteMemberBtn.addActionListener(e -> deleteStudent());

        // Load file event
        loadBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                currentFileName = fileChooser.getSelectedFile().getAbsolutePath();
                loadData();
            }
        });
    }

    // Hàm gọi API PHP để lấy dữ liệu user_data
    private void loadDataFromAPI() {
        new javax.swing.SwingWorker<java.util.List<model.ClassRoom>, Void>() {
            @Override
            protected java.util.List<model.ClassRoom> doInBackground() throws Exception {
                String url = "https://wuangvinh.id.vn/user_data.php";
                String urlParameters = "user_id=" + userId + "&action=get";
                byte[] postData = urlParameters.getBytes(java.nio.charset.StandardCharsets.UTF_8);

                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postData.length));
                try (java.io.DataOutputStream wr = new java.io.DataOutputStream(conn.getOutputStream())) {
                    wr.write(postData);
                }

                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                String json = response.toString();
                System.out.println("API response: " + json);
                if (json.contains("\"success\":true")) {
                    // Fix escape regex to correct Java syntax (must use 3 \\ for each \ in regex)
                    String data = json.replaceAll(".*\\\"data\\\":\\\"(.*?)\\\".*", "$1");
                    if (!data.isEmpty()) {
                        byte[] decodedBytes = java.util.Base64.getDecoder().decode(data);
                        String decoded = new String(decodedBytes, java.nio.charset.StandardCharsets.UTF_8);
                        return util.FileHandler.readClassesFromDatabaseString(decoded);
                    }
                }
                return new java.util.ArrayList<>();
            }

            @Override
            protected void done() {
                try {
                    java.util.List<model.ClassRoom> loadedClasses = get();
                    classRooms = loadedClasses;
                    classComboBox.removeAllItems();
                    for (model.ClassRoom c : classRooms) {
                        classComboBox.addItem(classDisplayString(c));
                    }
                    if (!classRooms.isEmpty()) {
                        classComboBox.setSelectedIndex(0);
                        currentClassIndex = 0;
                    } else {
                        currentClassIndex = -1;
                        tableModel.setRowCount(0);
                    }
                    refreshTable();
                    JOptionPane.showMessageDialog(StudentManagementGUI.this, "Load data from API success!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(StudentManagementGUI.this, "Load data failed! " + ex.getMessage());
                }
            }
        }.execute();
    }

    // PHP API call function to save user_data
    private void saveDataToAPI() {
        try {
            String url = "https://wuangvinh.id.vn/user_data.php";
           // Serialize class data to text (like when writing to a txt file)
            StringBuilder sb = new StringBuilder();
            for (model.ClassRoom c : classRooms) {
                sb.append("CLASS:" + c.getClassCode() + "|" + c.getSubjectName() + "|" + c.getClassName() + "|" + c.getAttendanceWeight() + "|" + c.getAssignmentWeight()).append("\n");
                for (model.Student s : c.getStudents()) {
                    String attendance = util.FileHandler.joinDoubleList(s.getAttendanceScores());
                    String assignments = util.FileHandler.joinDoubleList(s.getAssignmentScores());
                    String line = s.getId() + "," + s.getName() + "," + attendance + "," + assignments + "," + s.getFinalProjectScore();
                    sb.append(line).append("\n");
                }
            }
            String base64Data = java.util.Base64.getEncoder().encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8));
            String urlParameters = "user_id=" + userId + "&action=save&data=" + URLEncoder.encode(base64Data, "UTF-8");
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postData.length));
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            String json = response.toString();
            if (json.contains("\"success\":true")) {
                JOptionPane.showMessageDialog(this, "Save data to API success!");
            } else {
                JOptionPane.showMessageDialog(this, "Save data failed! " + json);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Save data failed! " + ex.getMessage());
        }
    }

    private void addClass() {
        JPanel panel = new JPanel(new GridLayout(3, 2));
        JTextField codeField = new JTextField();
        JTextField subjectField = new JTextField();
        JTextField nameField = new JTextField();
        panel.add(new JLabel("Class Code:"));
        panel.add(codeField);
        panel.add(new JLabel("Subject Name:"));
        panel.add(subjectField);
        panel.add(new JLabel("Class Name:"));
        panel.add(nameField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Enter new class information", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String classCode = codeField.getText().trim();
            String subjectName = subjectField.getText().trim();
            String className = nameField.getText().trim();
            if (!classCode.isEmpty() && !subjectName.isEmpty() && !className.isEmpty()) {
                model.ClassRoom newClass = new model.ClassRoom(classCode, subjectName, className);
                classRooms.add(newClass);
                classComboBox.addItem(classDisplayString(newClass));
                classComboBox.setSelectedIndex(classComboBox.getItemCount() - 1);
                // saveToFile(); // Removed autosave when creating class
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            }
        }
    }

    private String classDisplayString(model.ClassRoom c) {
        return c.getClassCode() + " - " + c.getSubjectName() + " - " + c.getClassName();
    }

    private void removeClass() {
        int idx = classComboBox.getSelectedIndex();
        if (idx >= 0) {
            classRooms.remove(idx);
            classComboBox.removeItemAt(idx);
            if (classComboBox.getItemCount() > 0) {
                classComboBox.setSelectedIndex(0);
            } else {
                currentClassIndex = -1;
                tableModel.setRowCount(0);
            }
        }
    }

    private void loadData() {
        classRooms = util.FileHandler.readClassesFromDatabase(userId);
        classComboBox.removeAllItems();
        for (model.ClassRoom c : classRooms) {
            classComboBox.addItem(classDisplayString(c));
        }
        if (!classRooms.isEmpty()) {
            classComboBox.setSelectedIndex(0);
            currentClassIndex = 0;
        } else {
            currentClassIndex = -1;
            tableModel.setRowCount(0);
        }
        refreshTable();
    }

    private void saveToFile() {
        util.FileHandler.saveClassesToDatabase(userId, classRooms);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        if (currentClassIndex < 0 || currentClassIndex >= classRooms.size()) return;
        ClassRoom c = classRooms.get(currentClassIndex);
        // Automatically sort by name
        c.getStudents().sort((s1, s2) -> {
            String lastName1 = s1.getName().trim();
            String lastName2 = s2.getName().trim();
            if (lastName1.contains(" ")) lastName1 = lastName1.substring(lastName1.lastIndexOf(' ') + 1);
            if (lastName2.contains(" ")) lastName2 = lastName2.substring(lastName2.lastIndexOf(' ') + 1);
            return lastName1.compareToIgnoreCase(lastName2);
        });
        for (Student s : c.getStudents()) {
            tableModel.addRow(new Object[]{
                s.getId(),
                s.getName(),
                joinDoubleList(s.getAttendanceScores()),
                joinDoubleList(s.getAssignmentScores()),
                String.format("%.2f", s.getPersonalScore(c)),
                s.getFinalProjectScore(),
                String.format("%.2f", s.getFinalGrade(c)),
                s.getFinalGradeLetter(c)
            });
        }
    }

    private void addStudent() {
        System.out.println("Start adding student...");
        if (currentClassIndex < 0 || currentClassIndex >= classRooms.size()) {
            JOptionPane.showMessageDialog(this, "Please select a class before adding a student.");
            return;
        }
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String attStr = attendanceField.getText().trim();
        String assignStr = assignmentField.getText().trim();
        String finalStr = finalProjectField.getText().trim();
        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both ID and Name.");
            return;
        }
        model.ClassRoom c = classRooms.get(currentClassIndex);
        if (c.findStudentById(id) != null) {
            JOptionPane.showMessageDialog(this, "ID already exists in this class.");
            return;
        }
        List<Double> attendance, assignments;
        double finalProject;
        try {
            attendance = parseDoubleList(attStr);
            assignments = parseDoubleList(assignStr);
            finalProject = parseDouble(finalStr);
            if (!validateScores(attendance) || !validateScores(assignments) || !validateScore(finalProject)) {
                throw new IllegalArgumentException();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid scores (numbers between 0 and 10, or a list separated by |)");
            return;
        }
        model.Student s = new model.Student(id, name);
        s.setAttendanceScores(attendance);
        s.setAssignmentScores(assignments);
        s.setFinalProjectScore(finalProject);
        c.addStudent(s);
        refreshTable();
        // saveToFile(); // Removed autosave when adding student
        System.out.println("Student added successfully!");
    }

    private void updateStudent() {
        if (currentClassIndex < 0) return;
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        List<Double> attendance = parseDoubleList(attendanceField.getText().trim());
        List<Double> assignments = parseDoubleList(assignmentField.getText().trim());
        double finalProject = parseDouble(finalProjectField.getText().trim());
        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter ID and Name.");
            return;
        }
        model.ClassRoom c = classRooms.get(currentClassIndex);
        Student s = c.findStudentById(id);
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Student not found.");
            return;
        }
        s.setName(name);
        s.setAttendanceScores(attendance);
        s.setAssignmentScores(assignments);
        s.setFinalProjectScore(finalProject);
        refreshTable();
        // saveToFile(); // Removed autosave when updating student
    }

    private void deleteStudent() {
        if (currentClassIndex < 0) return;
        model.ClassRoom c = classRooms.get(currentClassIndex);
        int selectedRow = table.getSelectedRow();
        String id = null;
        if (selectedRow >= 0) {
            id = tableModel.getValueAt(selectedRow, 0).toString();
        } else {
            id = idField.getText().trim();
        }
        System.out.println("Deleting student with ID: " + id);
        if (id == null || id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select or enter the ID of the student to delete.");
            return;
        }
        boolean removed = c.removeStudentById(id.trim());
        if (removed) {
            System.out.println("Student deleted successfully!");
            refreshTable();
            // saveToFile(); // Removed autosave when updating student
        } else {
            System.out.println("Student not found to delete!");
            JOptionPane.showMessageDialog(this, "Student not found.");
        }
    }

    private void updateStudentFromTable(int row) {
        if (currentClassIndex < 0) return;
        String id = tableModel.getValueAt(row, 0).toString();
        model.ClassRoom c = classRooms.get(currentClassIndex);
        Student s = c.findStudentById(id);
        if (s == null) return;
        s.setAttendanceScores(parseDoubleList(tableModel.getValueAt(row, 2).toString()));
        s.setAssignmentScores(parseDoubleList(tableModel.getValueAt(row, 3).toString()));
        s.setFinalProjectScore(parseDouble(tableModel.getValueAt(row, 5).toString()));
        // Update final grade and letter grade
        tableModel.setValueAt(String.format("%.2f", s.getFinalGrade(c)), row, 6);
        tableModel.setValueAt(s.getFinalGradeLetter(c), row, 7);
    }

    private List<Double> parseDoubleList(String str) {
        List<Double> list = new ArrayList<>();
        if (str == null || str.isEmpty()) return list;
        String[] arr = str.split("\\|");
        for (String s : arr) {
            try {
                list.add(Double.parseDouble(s));
            } catch (NumberFormatException ignored) {}
        }
        return list;
    }

    private String joinDoubleList(List<Double> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append("|");
        }
        return sb.toString();
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // Score validation helpers
    private boolean validateScores(List<Double> scores) {
        for (Double d : scores) {
            if (d == null || d < 0.0 || d > 10.0) return false;
        }
        return true;
    }
    private boolean validateScore(double score) {
        return score >= 0.0 && score <= 10.0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new gui.LoginRegisterFrame().setVisible(true));
    }

    // Set font for the entire GUI
    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
    }
} 