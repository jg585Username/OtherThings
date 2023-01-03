import java.awt.Color;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.event.*;
import flanagan.io.*;
import java.awt.Image;


public class HeatLine extends javax.swing.JFrame 
                                        implements MouseListener, MouseMotionListener,
                                                            ActionListener {
     
    protected final int gridCount=25;  //how many squares wide our drawing area is
    private double[] bar = new double[250];
    private int barWidth = 2;
    private int barHeight = 100;
    
    protected int[][] grid = new int[gridCount][gridCount]; //create the matrix    (row major)     
    private final int squareSize=20; //the size length of individual squares in pixels
    private final int gridSize=gridCount*squareSize;  //size of entire draw grid in pixels
    private final int offSet=50;  //how far from top/left edge do we draw the grid
    private int penColor = 1; //keeps track of current drawing color
    private Color[] colors; //our array of colors
    private Image ib;  //we do all drawing onto this image, it acts as an image buffer
    private Graphics ibg;  //will be set to our image buffer's graphic object
    private String filename;
    
    private Timer timmy;
    private boolean active=false;
    private int stepDelay = 500;
    private Color borderColor=new Color(20,20,20);
    
    boolean jMode=false;
    
    
    //constructor for our GridderFrame
    public HeatLine() {
        initComponents();
        addMouseListener(this);  //registers this frame to receive mouse clicks
        addMouseMotionListener(this); //register this frame to receive mouse motions
        setUpImageBuffer();
        takeCareOfResizing();
        setLocationRelativeTo(null);
        setUpColors();
        clearGrid();
        draw();
    }

    
    //set our image (buffer) to a new image of the correct size
    public void setUpImageBuffer(){
        ib=this.createImage(gridSize+1,gridSize+1);
        ibg=ib.getGraphics();
    }

    
    //experimental code that redraws the grid after user resizes the window
    //works some of the time... still in progress
    public void takeCareOfResizing(){
         this.addComponentListener(new ComponentAdapter() {
           public void componentResized(ComponentEvent e) {
              drawBar(); 
           }  
           public void componentMoved(ComponentEvent e) {
              drawBar();
           }
         });
    }
    
    public void setUpColors(){
        colors=new Color[2];
        colors[0]=Color.darkGray;  colors[1]=Color.white; 
        
    }
    
    
    //fills the grid with 1's (represents white!)
    public void clearGrid(){
        for(int r=0; r<gridCount; r++)
            for(int c=0; c<gridCount; c++)
                grid[r][c]=0;
        
        draw();
    } 
    
     
    public void mousePressed(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) {  }
    
    
    //will use the mouse x and y coordinates and figure out which square in the
    //draw area was clicked and pass this information to the clickGrid method
    public void mouseClicked(MouseEvent e) { 
        int x = e.getX(); int y = e.getY(); 
        System.out.println("Clicked at x value of " + x);
        int col = (x-offSet)/barWidth;
        System.out.println("Click bar segment: " + col);
        //if inside our grid, call clickGrid passing it the col and row
        if (isValid(col))
           clickBar(col);
    }
    
    public boolean isValid(int col){
         if ( (col>=0) && (col<bar.length) )
             return true;
         return false;
    }
  
    
    //will use the mouse x and y coordinates and figure out which square in the
    //draw area dragging is occurring and pass this information to the clickGrid method    
    public void mouseDragged(MouseEvent e) { 
      int x = e.getX(); int y = e.getY(); 
      int col = (x-offSet )/barWidth;
      System.out.println("Drag on bar segment:  " + col); 
      clickBar(col);
    }
    
    public void mouseMoved(MouseEvent e) { 
        //System.out.println("Moving at " + e.getX() + "," +  e.getY() );  
    }
    
    //set a square in the grid to the color value as long as the values are valid
    public void colorSquare(int row, int col, int colorValue){
        grid[row][col] = colorValue;
        draw();
    }
    
    
    //any mouse click is forwarded to this method with the col and row of the
    //square clicked provided as parameters.  Comes from public void mouseClicked.
    public void clickBar(int col ){
        System.out.println("Setting segment " + col+ " to 255");
        bar[col]=255;
           drawBar();
    }
    
    public void flood(int row, int col) {
        
    }
    
    
    //any mouse drag is forwarded to this method with the col and row of the
    //square dragged over provided as parameters.  Comes from public void mouseDragged.
    public void dragGrid( int row, int col) {
       
    }
    
    
    //draws the image based on the values stored in the grid.
    //we will draw on the image buffer's graphics object and then when we are
    //all done we will copy the image buffer onto the Frame's graphic object.
    public void draw(){
        
        //clear the area, draw white background
        ibg.clearRect(0, 0, gridSize,gridSize);
        ibg.setColor(Color.WHITE);
        ibg.fillRect(0, 0, gridSize, gridSize);
        
        //draws individual squares (pass this method the frames graphics object
        drawSquares(ibg);
        
        //draws a black border around edge of grid
        ibg.setColor(Color.black);
        ibg.drawRect(0,0,gridSize,gridSize);
        
        //all done drawing your stuff onto the image buffer?
        //get the frame's graphics object and draw our image buffer onto the frame
        Graphics g = this.getGraphics();
        g.drawImage(ib,offSet,offSet,this);
    }
    
    public void drawBar() {
        //clear the area, draw white background
        ibg.clearRect(0, 0, gridSize,gridSize);
        ibg.setColor(Color.gray);
        ibg.fillRect(0, 0, gridSize, gridSize);
        
        //draws individual squares (pass this method the frames graphics object
        drawBar(ibg);
        
        //draws a black border around edge of grid
        ibg.setColor(Color.black);
        ibg.drawRect(0,0,gridSize,gridSize);
        
        //all done drawing your stuff onto the image buffer?
        //get the frame's graphics object and draw our image buffer onto the frame
        Graphics g = this.getGraphics();
        g.drawImage(ib,offSet,offSet,this);    
    }
    
    public void drawBar(Graphics g) {
        for(int k=0; k<bar.length; k++) {
            double red;
            double value=bar[k];
            if (value<0)
                value=0;
            else if (value>255)
                value=255;
            red=value;
            Color c = new Color((int)red, 0, 0);
            g.setColor(c);
            g.fillRect(k*barWidth, 150, barWidth, barHeight);
            
        }
    }
    
    
    //draws the individual colored squares that make the grid using the values
    //stored in the grid matrix.
    public void drawSquares(Graphics g){
       //draw each square (remember that squareSize is size of each square...
       g.setColor(Color.black);
       for(int r=0; r<gridCount; r++){
           for(int c=0; c<gridCount; c++) {
               g.setColor(colors[ grid[r][c] ] );
               g.fillRect(c*squareSize, r*squareSize, squareSize, squareSize);      
               g.setColor(borderColor);
               g.drawRect(c*squareSize, r*squareSize, squareSize, squareSize);
           }
       }
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        textInfo = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        buttonOpen = new javax.swing.JButton();
        buttonSave = new javax.swing.JButton();
        buttonRefresh = new javax.swing.JButton();
        buttonClear = new javax.swing.JButton();
        buttonBlack = new javax.swing.JButton();
        buttonWhite = new javax.swing.JButton();
        buttonStep = new javax.swing.JButton();
        buttonCopy = new javax.swing.JButton();
        buttonPaste = new javax.swing.JButton();
        buttonRun = new javax.swing.JButton();
        jSliderDelay = new javax.swing.JSlider();
        jPanel1 = new javax.swing.JPanel();
        button1 = new javax.swing.JButton();
        buttonOneEndHot = new javax.swing.JButton();
        button3 = new javax.swing.JButton();
        button4 = new javax.swing.JButton();
        button5 = new javax.swing.JButton();
        button6 = new javax.swing.JButton();
        button7 = new javax.swing.JButton();
        button8 = new javax.swing.JButton();
        button9 = new javax.swing.JButton();
        button10 = new javax.swing.JButton();
        button11 = new javax.swing.JButton();
        button12 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 51));
        setMinimumSize(new java.awt.Dimension(950, 660));

        textInfo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        textInfo.setText("give user info here...");

        buttonOpen.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        buttonOpen.setText("Open");
        buttonOpen.setPreferredSize(new java.awt.Dimension(100, 20));
        buttonOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOpenActionPerformed(evt);
            }
        });

        buttonSave.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        buttonSave.setText("Save");
        buttonSave.setPreferredSize(new java.awt.Dimension(100, 20));
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveActionPerformed(evt);
            }
        });

        buttonRefresh.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        buttonRefresh.setText("Refresh");
        buttonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRefreshActionPerformed(evt);
            }
        });

        buttonClear.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        buttonClear.setText("Clear");
        buttonClear.setPreferredSize(new java.awt.Dimension(100, 20));
        buttonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClearActionPerformed(evt);
            }
        });

        buttonBlack.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        buttonBlack.setText("BLACK");
        buttonBlack.setMaximumSize(new java.awt.Dimension(50, 30));
        buttonBlack.setMinimumSize(new java.awt.Dimension(75, 0));
        buttonBlack.setPreferredSize(new java.awt.Dimension(50, 20));
        buttonBlack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBlackActionPerformed(evt);
            }
        });

        buttonWhite.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        buttonWhite.setText("WHITE");
        buttonWhite.setMaximumSize(new java.awt.Dimension(50, 30));
        buttonWhite.setMinimumSize(new java.awt.Dimension(75, 0));
        buttonWhite.setPreferredSize(new java.awt.Dimension(50, 20));
        buttonWhite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonWhiteActionPerformed(evt);
            }
        });

        buttonStep.setText("STEP");
        buttonStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStepActionPerformed(evt);
            }
        });

        buttonCopy.setText("Copy");

        buttonPaste.setText("Paste");

        buttonRun.setText("Run");
        buttonRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRunActionPerformed(evt);
            }
        });

        jSliderDelay.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jSliderDelay.setMajorTickSpacing(500);
        jSliderDelay.setMaximum(2000);
        jSliderDelay.setMinorTickSpacing(100);
        jSliderDelay.setPaintLabels(true);
        jSliderDelay.setPaintTicks(true);
        jSliderDelay.setSnapToTicks(true);
        jSliderDelay.setValue(500);
        jSliderDelay.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderDelayStateChanged(evt);
            }
        });

        button1.setText("Hot Bar");
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });

        buttonOneEndHot.setText("One End Hot");
        buttonOneEndHot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOneEndHotActionPerformed(evt);
            }
        });

        button3.setText("1 Random Hot Spot");
        button3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button3ActionPerformed(evt);
            }
        });

        button4.setText("3 Random Hot Spots");

        button5.setText("Cos Wave Hot Spots");

        button6.setText("06");

        button7.setText("07");

        button8.setText("08");

        button9.setText("09");

        button10.setText("10");
        button10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button10ActionPerformed(evt);
            }
        });

        button11.setText("11");
        button11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button11ActionPerformed(evt);
            }
        });

        button12.setText("12");
        button12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(button1, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                    .addComponent(buttonOneEndHot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 10, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {button1, button10, button11, button12, button3, button4, button5, button6, button7, button8, button9, buttonOneEndHot});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(button1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonOneEndHot)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(button3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(button4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(button5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(button6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(button7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(button8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(button9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(button10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(button11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(button12)
                .addGap(0, 9, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel1.setText("set delay");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSliderDelay, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(buttonSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(buttonClear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(buttonRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(buttonBlack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(buttonStep, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(buttonWhite, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(buttonCopy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(buttonPaste, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(buttonOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(buttonRun, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(28, 28, 28)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(156, 156, 156)
                        .addComponent(jLabel1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonBlack, buttonClear, buttonCopy, buttonOpen, buttonPaste, buttonRefresh, buttonRun, buttonSave, buttonStep, buttonWhite});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(buttonOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(buttonClear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonRefresh)
                        .addGap(18, 18, 18)
                        .addComponent(buttonCopy)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonPaste)
                        .addGap(20, 20, 20)
                        .addComponent(buttonBlack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonWhite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(buttonStep)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonRun)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSliderDelay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addContainerGap(184, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonBlack, buttonWhite});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonClear, buttonRefresh});

        jScrollPane1.setViewportView(jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 930, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 559, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

   
       
    //saves the current picture data in CS format
    private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveActionPerformed
      //String curDir = System.getProperty("user.dir");
      //System.out.println("Your directory is " + curDir);
      FileChooser FC = new FileChooser();
      FC.selectFile();
      filename=FC.getDirPath() +"/"+ FC.getFileName();
      if (filename==null || filename.length()<1)
          return;
      System.out.println("Filename: " + filename);
      FC.close();
      
      FileOutput FO = new FileOutput(filename,"w");
    
      for(int r=0; r<gridCount; r++) {
         for(int c=0; c<gridCount; c++) {
             int num = grid[r][c];
             if (num<10)
                 FO.print("0"+num);
             else
                 FO.print(num);
         }
         FO.println(""); //go to new line
      }
      FO.close();
        
    }//GEN-LAST:event_buttonSaveActionPerformed

    //opens a CS format file and loads the image data into the grid
    private void buttonOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOpenActionPerformed
      FileChooser FC = new FileChooser();
      FC.selectFile();
      filename=FC.getDirPath() +"/"+ FC.getFileName();
      System.out.println(filename);
      FC.close();
      
      FileInput FI = new FileInput(filename);
      int num=FI.numberOfLines();
      if (num!=gridCount){
          System.out.println("Not enough lines in file!");
          return;
      }
      
      //read each row.  go through row two characters at a time and convert to number
      for(int r=0; r<gridCount; r++) {
          String line = FI.readLine();                         //read the next line
          for(int c=0; c<gridCount; c++){                 //or (int c=0; c<gridCount*2; c=c+2)
              String temp=line.substring(c*2, c*2+2);   //0,2,4,6,8,...
              int value=Integer.parseInt(temp);           //convert to int
              grid[r][c]=value;                                  //put in grid
          }
      }
      FI.close();
      
      //lets see what you just read!
      draw();
      
    }//GEN-LAST:event_buttonOpenActionPerformed

    //redraws the canvas when the picture disappears
    private void buttonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRefreshActionPerformed
        draw();
    }//GEN-LAST:event_buttonRefreshActionPerformed

    private void buttonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonClearActionPerformed
        clearGrid();
    }//GEN-LAST:event_buttonClearActionPerformed


    private void buttonBlackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBlackActionPerformed
        penColor=0;
    }//GEN-LAST:event_buttonBlackActionPerformed

    private void buttonWhiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonWhiteActionPerformed
        penColor=1;
    }//GEN-LAST:event_buttonWhiteActionPerformed

    public boolean isAlive(int r, int c) {
        //if cell is in grid and alive, return true
        //outside grid or not alive, return false
        return(true);
    }
    
    private void buttonStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStepActionPerformed
        step();
    }//GEN-LAST:event_buttonStepActionPerformed

    public int liveNeighbors(int r, int c){
       return(0);
    }
    
    public void step() {
        //make the temperature values 'relax' based on their neighbor!
        
        //leave this in here or you won't see anything happen!
        drawBar();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        //this event is called by the timer...
        //System.out.println("Calling Step...");
        //System.out.println(e.getSource());
        step();
    }
    
    private void buttonRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRunActionPerformed
        //create timer if it doesn't exist.  If it does exist, turn it on!
        System.out.println("Run/Pause Button Pressed");
        if (active==false) {
            if (timmy==null) {
              timmy=new Timer(stepDelay,this);
              timmy.setInitialDelay(100); 
              timmy.setDelay(stepDelay);
            }
            
            timmy.start();
            active=true;
            buttonRun.setText("Pause...");
        }
        else { //turn timer off
            active=false;
            timmy.stop();
            buttonRun.setText("Run...");
        }
    }//GEN-LAST:event_buttonRunActionPerformed

    private void jSliderDelayStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderDelayStateChanged
        // TODO add your handling code here:
        stepDelay=jSliderDelay.getValue();
        if (stepDelay==0) {
            stepDelay=1;
        }
        if (timmy!=null)
            timmy.setDelay(stepDelay);
    }//GEN-LAST:event_jSliderDelayStateChanged

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
        

        draw();
    }//GEN-LAST:event_button1ActionPerformed

    private void button12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button12ActionPerformed

    private void button11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button11ActionPerformed
 
    }//GEN-LAST:event_button11ActionPerformed

 
    
    private void button10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button10ActionPerformed
    
    }//GEN-LAST:event_button10ActionPerformed

    private void buttonOneEndHotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOneEndHotActionPerformed
            for(int k=0; k<bar.length/6; k++) 
                bar[k]=255;
            for(int k=bar.length/6+1; k<bar.length; k++)
                bar[k]=0;
            drawBar();
            
            
    }//GEN-LAST:event_buttonOneEndHotActionPerformed

    private void button3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button3ActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HeatLine().setVisible(true);
            }
        });
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button1;
    private javax.swing.JButton button10;
    private javax.swing.JButton button11;
    private javax.swing.JButton button12;
    private javax.swing.JButton button3;
    private javax.swing.JButton button4;
    private javax.swing.JButton button5;
    private javax.swing.JButton button6;
    private javax.swing.JButton button7;
    private javax.swing.JButton button8;
    private javax.swing.JButton button9;
    private javax.swing.JButton buttonBlack;
    private javax.swing.JButton buttonClear;
    private javax.swing.JButton buttonCopy;
    private javax.swing.JButton buttonOneEndHot;
    private javax.swing.JButton buttonOpen;
    private javax.swing.JButton buttonPaste;
    private javax.swing.JButton buttonRefresh;
    private javax.swing.JButton buttonRun;
    private javax.swing.JButton buttonSave;
    private javax.swing.JButton buttonStep;
    private javax.swing.JButton buttonWhite;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSlider jSliderDelay;
    private javax.swing.JTextField textInfo;
    // End of variables declaration//GEN-END:variables

} //end of class