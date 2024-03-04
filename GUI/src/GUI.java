package GUI;

/**
 *
 */
import java.awt.BorderLayout;
//import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

public class GUI extends JFrame implements Runnable {
    public static final String APP_NAME = "8/15-Puzzle Solver";
    private static final long serialVersionUID = 1L;

    private final ApplicationStarter applicationStarter;
    private final DefaultListModel model;
    private final JButton shuffleButton, solveButton, showButton;
    private final JLabel statusLabel;
    private final JList directionsList;
    private final JTextField configurationField,
                             expandedField,
                             initialMovesEstimateField,
                             initialOrderField,
                             movesField,
                             pathsField,
                             statusField,
                             timeField;
    private final Puzzle puzzle;
    private final Timer statusUpdateTimer;

    private JMenu optionsMenu;
    private JRadioButtonMenuItem aStarItem,
                                 idaStarItem,
                                 singleThreadedItem,
                                 multiThreadedItem;
    private GraphicsThread graphicsThread;
    private int puzzleType, algorithmType, heuristicType, stepNumber;
    private boolean shouldRun, keepZeroInCorner, useThreads = true;
    private byte[] initState, graphicsState;

    public GUI(final ApplicationStarter appStarter) {
        super(APP_NAME);
        this.applicationStarter = appStarter;
        puzzleType = PuzzleConfiguration.PUZZLE_15;
        algorithmType = PuzzleConfiguration.ALGORITHM_IDASTAR;
        heuristicType = PuzzleConfiguration.HEURISTIC_PD;
        PuzzleConfiguration.initialize(
            puzzleType, algorithmType, heuristicType, getNumberOfThreads());

        statusUpdateTimer = new Timer(200, new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final Algorithm algorithm = PuzzleConfiguration.getAlgorithm();
                if (Algorithm.running || Algorithm.solved) {
                    initialMovesEstimateField.setText(
                        Algorithm.initialMovesEstimate == Algorithm.NOT_APPLICABLE ?
                            "N/A" :
                            Algorithm.initialMovesEstimate == 1 ?
                                Integer.toString(Algorithm.initialMovesEstimate) + " move" :
                                Integer.toString(Algorithm.initialMovesEstimate) + " moves");
                    movesField.setText(Long.toString(Algorithm.movesRequired));
                    pathsField.setText(
                        Utility.INT_FORMATTER.format(Algorithm.numberVisited));
                    expandedField.setText(
                        Utility.INT_FORMATTER.format(Algorithm.numberExpanded));
                }
                if (Algorithm.running) {
                    timeField.setText(
                        Utility.DEC_FORMATTER.format(
                            Algorithm.getElapsedTimeInSeconds()) + " s");
                } else {
                    if (Algorithm.solved) {
                        statusField.setText("找到了解决方案");
                        timeField.setText(
                            Utility.DEC_FORMATTER.format(
                                Algorithm.getRunningTimeInSeconds()) + " s");
                        final String[] directions = Utility.getDirections(initState);
                        for (int i = 0; i < directions.length; ++i) {
                            model.add(i, directions[i]);
                        }
                        showButton.setEnabled(true);
                    } else {
                        statusField.setText("被暂停");
                    }
                    configurationField.setEnabled(true);
                    shuffleButton.setEnabled(true);
                    optionsMenu.setEnabled(true);
                    solveButton.setText("Solve");
                    statusUpdateTimer.stop();
                    // Defer cleaning up until GUI has updated, otherwise a visible lag
                    // is possible.
                    algorithm.cleanup();
                }
            }
        });
        statusUpdateTimer.setInitialDelay(0);

        setJMenuBar(getCreatedMenuBar());

        configurationField = new JTextField(27);
        configurationField.addKeyListener(new KeyListener() {
            public void keyPressed(final KeyEvent ke) {
                if (KeyEvent.getKeyText(ke.getKeyCode()).equals("Enter")) {
                    solveButton.requestFocus();
                    solvePuzzle();
                }
            }
            public void keyReleased(final KeyEvent ke) { }
            public void keyTyped(final KeyEvent ke) { }
        });

        final JLabel configurationLabel =
            new JLabel("Tile order:", JLabel.RIGHT);
        configurationLabel.setDisplayedMnemonic('T');
        configurationLabel.setLabelFor(configurationField);

        solveButton = new JButton("Solve");
        solveButton.setMnemonic('S');
        solveButton.setPreferredSize(new Dimension(88, 26));
        solveButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                if (solveButton.getText().equals("Solve")) {
                    solvePuzzle();
                } else {
                    stop();
                }
            }
        });

        shuffleButton = new JButton("随机");
        shuffleButton.setMnemonic('u');
        shuffleButton.setPreferredSize(new Dimension(88, 26));
        shuffleButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                cancelGraphicsThread();
                showButton.setEnabled(false);
                final byte[] state = Utility.getRandomArray
                    (puzzleType == PuzzleConfiguration.PUZZLE_15 ? 16 : 9, keepZeroInCorner);
                configurationField.setText(Utility.byteArrayToString(state));
                puzzle.setOrder(state);
            }
        });

        final JPanel topPanel = new JPanel();
        topPanel.add(configurationLabel);
        topPanel.add(configurationField);
        topPanel.add(shuffleButton);
        topPanel.add(solveButton);
        topPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "输入"));

        showButton = new JButton("展示");
        showButton.setMnemonic('M');
        showButton.setPreferredSize(new Dimension(125, 26));
        showButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                cancelGraphicsThread();
                graphicsState = new byte[initState.length];
                System.arraycopy(initState, 0, graphicsState, 0, initState.length);
                stepNumber = 0;
                graphicsThread = new GraphicsThread();
                graphicsThread.start();
            }
        });
        showButton.setEnabled(false);

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(showButton);

        puzzle = new Puzzle(PuzzleConfiguration.getNumOfTiles());
        final JPanel puzzleBorderPanel = new JPanel();
        puzzleBorderPanel.setLayout(new FlowLayout());
        puzzleBorderPanel.add(puzzle);

        final GridBagConstraints gbc = new GridBagConstraints();
        final JPanel puzzlePanel = new JPanel();
        puzzlePanel.setLayout(new GridBagLayout());
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 5, 5, 5);
        puzzlePanel.add(puzzleBorderPanel, gbc);
        puzzlePanel.add(buttonPanel, gbc);
        puzzlePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Puzzle"));

        statusField = new JTextField(10);
        statusField.setEditable(false);
        statusField.setBackground(Color.white);
        initialMovesEstimateField = new JTextField(10);
        initialMovesEstimateField.setEditable(false);
        initialMovesEstimateField.setBackground(Color.white);
        initialOrderField = new JTextField(10);
        initialOrderField.setEditable(false);
        initialOrderField.setBackground(Color.white);
        timeField = new JTextField(10);
        timeField.setEditable(false);
        timeField.setBackground(Color.white);
        movesField = new JTextField(10);
        movesField.setEditable(false);
        movesField.setBackground(Color.white);
        pathsField = new JTextField(10);
        pathsField.setEditable(false);
        pathsField.setBackground(Color.white);
        expandedField = new JTextField(10);
        expandedField.setEditable(false);
        expandedField.setBackground(Color.white);
        model = new DefaultListModel();
        directionsList = new JList(model);
        directionsList.setDoubleBuffered(true);
        directionsList.setPrototypeCellValue("10 - right");
        final JScrollPane scrollPane = new JScrollPane(directionsList);
        scrollPane.setPreferredSize(
            new Dimension(210, directionsList.getFixedCellHeight() * 6 + 3));

        final JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridBagLayout());

        gbc.insets = new Insets(5, 0, 1, 10);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        infoPanel.add(new JLabel("当前状态:", JLabel.RIGHT), gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(statusField, gbc);

        gbc.insets = new Insets(1, 0, 1, 10);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        infoPanel.add(new JLabel("搜索时间:", JLabel.RIGHT), gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(timeField, gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        infoPanel.add(new JLabel("初始状态:", JLabel.RIGHT), gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(initialOrderField, gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        infoPanel.add(new JLabel("最初的预计步数:", JLabel.RIGHT), gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(initialMovesEstimateField, gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        infoPanel.add(new JLabel("创建的路径数:", JLabel.RIGHT), gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(pathsField, gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        infoPanel.add(new JLabel("创建的节点数:", JLabel.RIGHT), gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(expandedField, gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        infoPanel.add(new JLabel("步数:", JLabel.RIGHT), gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(movesField, gbc);

        gbc.insets = new Insets(1, 0, 10, 10);
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        infoPanel.add(new JLabel("路径:", JLabel.RIGHT), gbc);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(scrollPane, gbc);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "输出"));

        final JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        centerPanel.add(puzzlePanel, gbc);
        gbc.gridx = 1;
        centerPanel.add(infoPanel, gbc);

        statusLabel = new JLabel(PuzzleConfiguration.stringRepresentation(), JLabel.RIGHT);
        final JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new GridBagLayout());
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        gbc.insets = new Insets(0, 0, 1, 3);
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        statusPanel.add(statusLabel, gbc);

        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(centerPanel, BorderLayout.CENTER);
        contentPane.add(statusPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new ClosingWindowListener(this));

        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void stop() {
        PuzzleConfiguration.getAlgorithm().stop();
        cancelGraphicsThread();
    }

    public void run() {
        PuzzleConfiguration.getAlgorithm().solve(
            Utility.arrayToLong(initState), PuzzleConfiguration.getNumOfThreads());
    }

    private void cancelGraphicsThread() {
        shouldRun = false;
        puzzle.stopAnimation();
        if (graphicsThread != null) {
            try {
                graphicsThread.join();
            } catch (final InterruptedException ie) { }
        }
        directionsList.clearSelection();
    }

    private JMenuBar getCreatedMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        final JMenu fileMenu = new JMenu("退出");
        fileMenu.setMnemonic('F');

        KeyStroke ks;
        final JMenuItem exitItem = new JMenuItem("确定");
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.ALT_MASK);
        exitItem.setAccelerator(ks);
        exitItem.setMnemonic(KeyEvent.VK_X);
        exitItem.addActionListener(new ExitActionListener(this));
        fileMenu.add(exitItem);

        optionsMenu = new JMenu("选项");
        optionsMenu.setMnemonic('O');

        final JMenu puzzleTypeMenu = new JMenu("选择Puzzle");
        puzzleTypeMenu.setMnemonic('P');

        final ButtonGroup puzzleTypeGroup = new ButtonGroup();
        final JRadioButtonMenuItem eightPuzzleItem =
            new JRadioButtonMenuItem("8-Puzzle",
                                     puzzleType == PuzzleConfiguration.PUZZLE_8);
        eightPuzzleItem.setMnemonic('8');
        eightPuzzleItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                cancelGraphicsThread();
                showButton.setEnabled(false);
                puzzleType = PuzzleConfiguration.PUZZLE_8;
                aStarItem.setEnabled(true);
                singleThreadedItem.setSelected(true);
                useThreads = false;
                multiThreadedItem.setEnabled(false);
                PuzzleConfiguration.setNumOfThreads(getNumberOfThreads());
                updatePuzzleConfiguration();
                puzzle.setNumOfTiles(puzzleType == PuzzleConfiguration.PUZZLE_15 ? 16 : 9);
            }
        });
        puzzleTypeGroup.add(eightPuzzleItem);
        final JRadioButtonMenuItem fifteenPuzzleItem =
        	new JRadioButtonMenuItem("15-Puzzle",
                                     puzzleType == PuzzleConfiguration.PUZZLE_15);
        fifteenPuzzleItem.setMnemonic('1');
        fifteenPuzzleItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                cancelGraphicsThread();
                showButton.setEnabled(false);
                puzzleType = PuzzleConfiguration.PUZZLE_15;
                if (aStarItem.isSelected()) {
                    idaStarItem.setSelected(true);
                    algorithmType = PuzzleConfiguration.ALGORITHM_IDASTAR;
                }
                aStarItem.setEnabled(false);
                if (canRunThreads()) {
                    multiThreadedItem.setEnabled(true);
                }
                PuzzleConfiguration.setNumOfThreads(getNumberOfThreads());
                updatePuzzleConfiguration();
                puzzle.setNumOfTiles(puzzleType == PuzzleConfiguration.PUZZLE_15 ? 16 : 9);
            }
        });
        puzzleTypeGroup.add(fifteenPuzzleItem);

        puzzleTypeMenu.add(eightPuzzleItem);
        puzzleTypeMenu.add(fifteenPuzzleItem);

        final JMenu algorithmTypeMenu = new JMenu("选择搜索算法");
        algorithmTypeMenu.setMnemonic('A');

        final ButtonGroup algorithmTypeGroup = new ButtonGroup();
        idaStarItem = new JRadioButtonMenuItem(
        	"IDA*", algorithmType == PuzzleConfiguration.ALGORITHM_IDASTAR);
        idaStarItem.setMnemonic('I');
        idaStarItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                cancelGraphicsThread();
                showButton.setEnabled(false);
                algorithmType = PuzzleConfiguration.ALGORITHM_IDASTAR;
                updatePuzzleConfiguration();
            }
        });
        algorithmTypeGroup.add(idaStarItem);
        aStarItem = new JRadioButtonMenuItem(
        	"A*", algorithmType == PuzzleConfiguration.ALGORITHM_ASTAR);
        aStarItem.setMnemonic('A');
        aStarItem.setEnabled(false);
        aStarItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                cancelGraphicsThread();
                showButton.setEnabled(false);
                algorithmType = PuzzleConfiguration.ALGORITHM_ASTAR;
                updatePuzzleConfiguration();
            }
        });
        algorithmTypeGroup.add(aStarItem);

        algorithmTypeMenu.add(idaStarItem);
        algorithmTypeMenu.add(aStarItem);

        final JMenu heuristicTypeMenu = new JMenu("选择启发函数");
        heuristicTypeMenu.setMnemonic('H');

        final ButtonGroup heuristicTypeGroup = new ButtonGroup();
        final JRadioButtonMenuItem patternDatabaseItem =
            new JRadioButtonMenuItem(
            	"模式数据库", heuristicType == PuzzleConfiguration.HEURISTIC_PD);
        patternDatabaseItem.setMnemonic('P');
        patternDatabaseItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                cancelGraphicsThread();
                showButton.setEnabled(false);
                heuristicType = PuzzleConfiguration.HEURISTIC_PD;
                updatePuzzleConfiguration();
            }
        });
        heuristicTypeGroup.add(patternDatabaseItem);
        final JRadioButtonMenuItem linearConflictItem =
            new JRadioButtonMenuItem("曼哈顿算法",
            	heuristicType == PuzzleConfiguration.HEURISTIC_LC);
        linearConflictItem.setMnemonic('L');
        linearConflictItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                cancelGraphicsThread();
                showButton.setEnabled(false);
                heuristicType = PuzzleConfiguration.HEURISTIC_LC;
                updatePuzzleConfiguration();
            }
        });
        heuristicTypeGroup.add(linearConflictItem);
        final JRadioButtonMenuItem manhattanDistanceItem =
            new JRadioButtonMenuItem("不在位将牌",
                heuristicType == PuzzleConfiguration.HEURISTIC_MD);
        manhattanDistanceItem.setMnemonic('M');
        manhattanDistanceItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                cancelGraphicsThread();
                showButton.setEnabled(false);
                heuristicType = PuzzleConfiguration.HEURISTIC_MD;
                updatePuzzleConfiguration();
            }
        });
        heuristicTypeGroup.add(manhattanDistanceItem);

        heuristicTypeMenu.add(patternDatabaseItem);
        heuristicTypeMenu.add(linearConflictItem);
        heuristicTypeMenu.add(manhattanDistanceItem);

        final JMenu threadingTypeMenu = new JMenu("线程模式");
        threadingTypeMenu.setMnemonic('T');
        final ButtonGroup threadingTypeGroup = new ButtonGroup();
        singleThreadedItem = new JRadioButtonMenuItem("单线程", false);
        singleThreadedItem.setMnemonic('S');
        singleThreadedItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                useThreads = false;
                PuzzleConfiguration.setNumOfThreads(getNumberOfThreads());
                statusLabel.setText(PuzzleConfiguration.stringRepresentation());
            }
        });
        multiThreadedItem = new JRadioButtonMenuItem("多线程", true);
        multiThreadedItem.setMnemonic('M');
        multiThreadedItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                useThreads = true;
                PuzzleConfiguration.setNumOfThreads(getNumberOfThreads());
                statusLabel.setText(PuzzleConfiguration.stringRepresentation());
            }
        });
        if (!canRunThreads()) {
            multiThreadedItem.setEnabled(false);
            singleThreadedItem.setSelected(true);
        }

        threadingTypeGroup.add(singleThreadedItem);
        threadingTypeGroup.add(multiThreadedItem);

        threadingTypeMenu.add(singleThreadedItem);
        threadingTypeMenu.add(multiThreadedItem);

        final JMenu shuffleTypeMenu = new JMenu("随机模式");
        shuffleTypeMenu.setMnemonic('S');

        final ButtonGroup shuffleTypeGroup = new ButtonGroup();
        final JRadioButtonMenuItem shuffleAllItem =
            new JRadioButtonMenuItem("全随机", true);
        shuffleAllItem.setMnemonic('A');
        shuffleAllItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                keepZeroInCorner = false;
            }
        });
        final JRadioButtonMenuItem shuffleExcludeSpaceItem =
            new JRadioButtonMenuItem("保持空缺在右下角", false);
        shuffleExcludeSpaceItem.setMnemonic('K');
        shuffleExcludeSpaceItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                keepZeroInCorner = true;
            }
        });

        shuffleTypeGroup.add(shuffleAllItem);
        shuffleTypeGroup.add(shuffleExcludeSpaceItem);

        shuffleTypeMenu.add(shuffleAllItem);
        shuffleTypeMenu.add(shuffleExcludeSpaceItem);

        optionsMenu.add(puzzleTypeMenu);
        optionsMenu.add(algorithmTypeMenu);
        optionsMenu.add(heuristicTypeMenu);
        optionsMenu.add(threadingTypeMenu);
        optionsMenu.add(new JSeparator());
        optionsMenu.add(shuffleTypeMenu);

        final JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setMnemonic('A');
        aboutItem.addActionListener(new AboutActionListener(this));

        final JMenu helpMenu = new JMenu("小组");
        helpMenu.setMnemonic('H');
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private void updatePuzzleConfiguration() {
        PuzzleConfiguration.initialize(
            puzzleType, algorithmType, heuristicType, getNumberOfThreads());
        statusLabel.setText(PuzzleConfiguration.stringRepresentation());
    }

    private void highlightInput() {
        configurationField.requestFocus();
        configurationField.selectAll();
    }

    private void solvePuzzle() {
        try {
            cancelGraphicsThread();
            showButton.setEnabled(false);
            final int numOfTiles = puzzleType == PuzzleConfiguration.PUZZLE_15 ? 16 : 9;
            initState = Utility.getArray(configurationField.getText(), numOfTiles);
            final String initialOrder = Utility.byteArrayToString(initState);
            configurationField.setText(initialOrder);
            puzzle.setOrder(initState);
            if (!Utility.isValidPermutation(initState)) {
                clearFields();
                new MessageBox(
                    this, "Information", "Puzzle无解.",
                    MessageBox.INFORM);
                highlightInput();
                return;
            }
            configurationField.setEnabled(false);
            shuffleButton.setEnabled(false);
            optionsMenu.setEnabled(false);
            clearFields();
            solveButton.setText("停止");
            statusField.setText("正在搜索...");
            initialOrderField.setText(initialOrder);
            initialOrderField.setCaretPosition(0);
            PuzzleConfiguration.getAlgorithm().start();
            final Thread t = new Thread(this);
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
            statusUpdateTimer.restart();
        } catch (final IllegalArgumentException iae) {
            new MessageBox(this, "Error", iae.getMessage(), MessageBox.EXCLAIM);
            highlightInput();
        }
    }

    private void clearFields() {
        statusField.setText("");
        initialOrderField.setText("");
        initialMovesEstimateField.setText("");
        timeField.setText("");
        movesField.setText("");
        pathsField.setText("");
        expandedField.setText("");
        model.clear();
    }

    private void doApplicationClosing(final JFrame parent) {
        if (applicationStarter != null) {
            applicationStarter.close();
        } else {
            System.exit(0);
        }
    }

    private boolean canRunThreads() {
        if (applicationStarter != null && Utility.getHasFullPermission() ||
            applicationStarter == null) {
            return true;
        }
        return false;
    }

    private int getNumberOfThreads() {
        if (algorithmType == PuzzleConfiguration.ALGORITHM_IDASTAR) {
            if (puzzleType == PuzzleConfiguration.PUZZLE_8) {
                return 1;
            } else {
                if (canRunThreads() && useThreads) {
                    return Utility.getDefaultNumOfThreads();
                } else {
                    return 1;
                }
            }
        }
        return 1;
    }

    class GraphicsThread extends Thread {
        public void run() {
            puzzle.setOrder(initState);
            shouldRun = true;
            while (shouldRun && stepNumber < Algorithm.shortestPath.length()) {
                Utility.generateNextState(
                    Algorithm.shortestPath.charAt(stepNumber), graphicsState);
                directionsList.setSelectedIndex(stepNumber);
                directionsList.ensureIndexIsVisible(stepNumber);
                directionsList.repaint();
                puzzle.animatePuzzleTo(graphicsState);
                ++stepNumber;
            }
            directionsList.clearSelection();
        }
    }

    class ExitActionListener implements ActionListener {
        private final JFrame parent;

        public ExitActionListener(final JFrame parent) {
            this.parent = parent;
        }

        public void actionPerformed(final ActionEvent e) {
            doApplicationClosing(parent);
        }
    }

    class AboutActionListener implements ActionListener {
        private final JFrame parent;

        public AboutActionListener(final JFrame parent) {
            this.parent = parent;
        }

        public void actionPerformed(final ActionEvent e) {
            final String[] data = { "8/15码 N-Puzzle问题",
                    "2022秋季人工智能15组",
                    "GUI：张汉清",
                    "小组成员：陈子琪 成子昂 赵梓奥 张汉清 马梦珂 李双颖" };
            new AboutDialog(parent, APP_NAME, data,
                "images/icon.png");
        }
    }

    class ClosingWindowListener implements WindowListener {
        private final JFrame parent;

        public ClosingWindowListener(final JFrame parent) {
            this.parent = parent;
        }

        public void windowClosing(final WindowEvent e) {
            doApplicationClosing(parent);
        }

        public void windowDeactivated(final WindowEvent e) { }

        public void windowActivated(final WindowEvent e) { }

        public void windowDeiconified(final WindowEvent e) { }

        public void windowIconified(final WindowEvent e) { }

        public void windowClosed(final WindowEvent e) { }

        public void windowOpened(final WindowEvent e) { }
    }
}

