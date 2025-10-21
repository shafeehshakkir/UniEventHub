package ui;

import models.Event;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

@SuppressWarnings("serial")
public class OrganizerDashboard extends JFrame {
    private User organizer;
    private DefaultTableModel eventTableModel;
    private JTable eventTable;

    private JTextField titleField = new JTextField(20);
    private JTextArea descriptionArea = new JTextArea(4, 20);
    private JTextField dateField = new JTextField(10); // yyyy-mm-dd

    public OrganizerDashboard(User organizer) {
        this.organizer = organizer;
        setTitle("Organizer Dashboard - " + organizer.getName());
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top panel for creating event
        JPanel createPanel = new JPanel(new GridBagLayout());
        createPanel.setBorder(BorderFactory.createTitledBorder("Create New Event"));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.LINE_START;

        gbc.gridx = 0; gbc.gridy = 0;
        createPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        createPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        createPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        createPanel.add(descScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        createPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        createPanel.add(dateField, gbc);

        JButton createBtn = new JButton("Create Event");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        createPanel.add(createBtn, gbc);

        mainPanel.add(createPanel, BorderLayout.NORTH);

        // Center panel for event list
        eventTableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Description", "Date"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        eventTable = new JTable(eventTableModel);
        JScrollPane tableScroll = new JScrollPane(eventTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Your Events"));
        mainPanel.add(tableScroll, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton deleteEventBtn = new JButton("Delete Selected Event");
        JButton logoutBtn = new JButton("Logout");
        JButton deleteProfileBtn = new JButton("Delete Profile");

        bottomPanel.add(deleteEventBtn);
        bottomPanel.add(logoutBtn);
        bottomPanel.add(deleteProfileBtn);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        createBtn.addActionListener(e -> createEvent());
        deleteEventBtn.addActionListener(e -> deleteSelectedEvent());

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new ui.LoginWindow().setVisible(true);
            }
        });

        deleteProfileBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Deleting your profile will remove all your events and registrations. Continue?",
                    "Delete Profile", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (organizer.deleteProfile()) {
                    JOptionPane.showMessageDialog(this, "Profile deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                    new ui.LoginWindow().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete profile.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        loadEvents();
    }

    private void createEvent() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String dateStr = dateField.getText().trim();

        if (title.isEmpty() || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and Date are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date date;
        try {
            date = Date.valueOf(dateStr);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Date format invalid. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Event event = new Event(organizer.getId(), title, description, date);
        if (event.save()) {
            JOptionPane.showMessageDialog(this, "Event created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearCreateForm();
            loadEvents();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create event.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearCreateForm() {
        titleField.setText("");
        descriptionArea.setText("");
        dateField.setText("");
    }

    private void loadEvents() {
        eventTableModel.setRowCount(0);
        List<Event> events = Event.getEventsByOrganizer(organizer.getId());
        for (Event e : events) {
            eventTableModel.addRow(new Object[]{
                    e.getId(),
                    e.getTitle(),
                    e.getDescription(),
                    e.getEventDate().toString()
            });
        }
    }

    private void deleteSelectedEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int eventId = (int) eventTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this event?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (Event.deleteEvent(eventId, organizer.getId())) {
                JOptionPane.showMessageDialog(this, "Event deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEvents();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete event.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}