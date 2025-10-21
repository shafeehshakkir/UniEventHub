package ui;

import models.Event;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@SuppressWarnings("serial")
public class StudentDashboard extends JFrame {
    private User student;

    private DefaultTableModel availableEventsModel;
    private JTable availableEventsTable;

    private DefaultTableModel registeredEventsModel;
    private JTable registeredEventsTable;

    public StudentDashboard(User student) {
        this.student = student;

        setTitle("Student Dashboard - " + student.getName());
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // Available Events Tab
        JPanel availablePanel = new JPanel(new BorderLayout());
        availableEventsModel = new DefaultTableModel(new Object[]{"ID", "Title", "Description", "Date"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        availableEventsTable = new JTable(availableEventsModel);
        JScrollPane availableScroll = new JScrollPane(availableEventsTable);
        availableScroll.setBorder(BorderFactory.createTitledBorder("Available Events"));
        availablePanel.add(availableScroll, BorderLayout.CENTER);

        JButton registerBtn = new JButton("Register for Selected Event");
        availablePanel.add(registerBtn, BorderLayout.SOUTH);

        // Registered Events Tab
        JPanel registeredPanel = new JPanel(new BorderLayout());
        registeredEventsModel = new DefaultTableModel(new Object[]{"ID", "Title", "Description", "Date"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        registeredEventsTable = new JTable(registeredEventsModel);
        JScrollPane registeredScroll = new JScrollPane(registeredEventsTable);
        registeredScroll.setBorder(BorderFactory.createTitledBorder("Your Registered Events"));
        registeredPanel.add(registeredScroll, BorderLayout.CENTER);

        tabbedPane.addTab("Available Events", availablePanel);
        tabbedPane.addTab("Registered Events", registeredPanel);

        add(tabbedPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton logoutBtn = new JButton("Logout");
        JButton deleteProfileBtn = new JButton("Delete Profile");

        bottomPanel.add(logoutBtn);
        bottomPanel.add(deleteProfileBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        registerBtn.addActionListener(e -> registerForEvent());

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new ui.LoginWindow().setVisible(true);
            }
        });

        deleteProfileBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Deleting your profile will remove all your registrations. Continue?",
                    "Delete Profile", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (student.deleteProfile()) {
                    JOptionPane.showMessageDialog(this, "Profile deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                    new ui.LoginWindow().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete profile.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        loadAvailableEvents();
        loadRegisteredEvents();
    }

    private void loadAvailableEvents() {
        availableEventsModel.setRowCount(0);
        List<Event> events = Event.getAllEvents();

        // Exclude events already registered
        List<Event> registered = Event.getEventsRegisteredByStudent(student.getId());
        java.util.Set<Integer> registeredIds = new java.util.HashSet<>();
        for (Event e : registered) {
            registeredIds.add(e.getId());
        }

        for (Event e : events) {
            if (!registeredIds.contains(e.getId())) {
                availableEventsModel.addRow(new Object[]{
                        e.getId(),
                        e.getTitle(),
                        e.getDescription(),
                        e.getEventDate().toString()
                });
            }
        }
    }

    private void loadRegisteredEvents() {
        registeredEventsModel.setRowCount(0);
        List<Event> events = Event.getEventsRegisteredByStudent(student.getId());
        for (Event e : events) {
            registeredEventsModel.addRow(new Object[]{
                    e.getId(),
                    e.getTitle(),
                    e.getDescription(),
                    e.getEventDate().toString()
            });
        }
    }

    private void registerForEvent() {
        int selectedRow = availableEventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to register.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int eventId = (int) availableEventsModel.getValueAt(selectedRow, 0);
        boolean success = Event.registerStudentToEvent(student.getId(), eventId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Registered successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAvailableEvents();
            loadRegisteredEvents();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to register or already registered.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}