import java.awt.Color;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.event.*;
import flanagan.io.*;
import java.awt.Image;
import java.util.ArrayList;


public class PathFinder extends javax.swing.JFrame 
                                        implements MouseListener, MouseMotionListener,
                                                            ActionListener {
     
    protected final int gridCount=20;  //how many squares wide our drawing area is
    protected int[][] grid = new int[gridCount][gridCount]; //create the matrix  
    protected int[][] pathGrid=new int[gridCount][gridCount]; //matrix used for path finding
    private int homeCol, homeRow, targetCol, targetRow;
    private ArrayList<Integer> mins=new ArrayList();
    private final int squareSize=25; //the size length of individual squares in pixels
    private final int gridSize=gridCount*squareSize;  //size of entire draw grid in pixels
    private final int offSet=50;  //how far from top/left edge do we draw the grid
    private int penColor = 0; //keeps track of current drawing color
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
    public PathFinder() {
        initComponents();
        addMouseListener(this);  //registers this frame to receive mouse clicks
        addMouseMotionListener(this); //register this frame to receive mouse motions
        setUpImageBuffer();
        takeCareOfResizing();
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
              draw(); 
           }  
           public void componentMoved(ComponentEvent e) {
              draw();
           }
         });
    }
    
    public void setUpColors(){
        colors=new Color[5];
        colors[1]=Color.darkGray;  colors[0]=Color.white; 
        colors[2]=Color.red; colors[3]=Color.blue; colors[4]=Color.green;
    }
    
    
    //fills the grid with 1's (represents white!)
    public void clearGrid(){
        for(int c=0; c<gridCount; c++)
            for(int r=0; r<gridCount; r++)
                grid[c][r]=0;
        
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
        int row = (y-offSet)/squareSize;
        int col = (x-offSet)/squareSize;
        System.out.println("Click: " + col + ", " + row);
        //if inside our grid, call clickGrid passing it the col and row
        if ( (row>=0) && (row<gridCount) && (col>=0) && (col<gridCount) )
           clickGrid(col, row);
    }
  
    
    //will use the mouse x and y coordinates and figure out which square in the
    //draw area dragging is occurring and pass this information to the clickGrid method    
    public void mouseDragged(MouseEvent e) { 
      int x = e.getX(); int y = e.getY(); 
      int row = (y-offSet )/squareSize;
      int col = (x-offSet )/squareSize;
      System.out.println("Drag:  " + col + ", " + row);
      //if inside our grid, call dragGrid passint it the col and row
      if ( (row>=0) && (row<gridCount) && (col>=0) && (col<gridCount) )
         dragGrid(col, row);       
    }
    
    public void mouseMoved(MouseEvent e) { 
        //System.out.println("Moving at " + e.getX() + "," +  e.getY() );  
    }
    
    //set a square in the grid to the color value as long as the values are valid
    public void colorSquare(int col, int row, int colorValue){
        grid[col][row] = colorValue;
        draw();
    }
    
    
    //any mouse click is forwarded to this method with the col and row of the
    //square clicked provided as parameters.  Comes from public void mouseClicked.
    public void clickGrid( int col, int row ){
         colorSquare(col, row, penColor); 
    }
 
    
    //any mouse drag is forwarded to this method with the col and row of the
    //square dragged over provided as parameters.  Comes from public void mouseDragged.
    public void dragGrid( int col, int row) {
        colorSquare(col, row, penColor);   
    }
    
    
    //draws the image based on the values stored in the grid.
    //we will draw on the image buffer's graphics object and then when we are
    //all done we will copy the image buffer onto the Frame's graphic object.
    public void draw(){
        
        //clear the area, draw white background
        ibg.clearRect(0, 0, gridSize,gridSize);
        ibg.setColor(Color.white);
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
    
    public void drawPathGrid(){ 
        //clear the area, draw white background
        ibg.clearRect(0, 0, gridSize,gridSize);
        ibg.setColor(Color.white);
        ibg.fillRect(0, 0, gridSize, gridSize);
        
        //draws individual squares (pass this method the frames graphics object
        drawPathSquares(ibg);
        
        //draws a black border around edge of grid
        ibg.setColor(Color.black);
        ibg.drawRect(0,0,gridSize,gridSize);
        
        //all done drawing your stuff onto the image buffer?
        //get the frame's graphics object and draw our image buffer onto the frame
        Graphics g = this.getGraphics();
        g.drawImage(ib,offSet,offSet,this);
    }
    
    //draws the individual colored squares that make the grid using the values
    //stored in the grid matrix.
    public void drawSquares(Graphics g){
       //draw each square (remember that squareSize is size of each square...
       g.setColor(Color.black);
       for(int r=0; r<gridCount; r++){
           for(int c=0; c<gridCount; c++) {
               //draw square black if barrier ( value -1)
               if (grid[c][r]>=0)
                   g.setColor(Color.white);
               else if (grid[c][r]<0)
                   g.setColor(Color.darkGray);
               
               g.fillRect(c*squareSize, r*squareSize, squareSize, squareSize);      
               g.setColor(borderColor);
               g.drawRect(c*squareSize, r*squareSize, squareSize, squareSize);
               
               //draw value if this isn't a barrier (not -1 value)
               if (grid[c][r]>=0) {
                   g.setColor(Color.orange);
                   g.drawString(""+grid[c][r], c*squareSize+(int)(squareSize/4.0), r*squareSize+15);
               }
           }
       }
    }
    
    
    public void drawPathSquares(Graphics g){
       //draw each square (remember that squareSize is size of each square...
       g.setColor(Color.black);
       for(int r=0; r<gridCount; r++){
           for(int c=0; c<gridCount; c++) {
               //draw square black if barrier ( value -1)
               if (pathGrid[c][r]>=0)
                   g.setColor(Color.white);
               else if (pathGrid[c][r]<0)
                   g.setColor(Color.darkGray);
               if (c==homeCol && r==homeRow)
                   g.setColor(Color.GREEN);
               else if (c==targetCol && r==targetRow)
                   g.setColor(Color.YELLOW);
               
               g.fillRect(c*squareSize, r*squareSize, squareSize, squareSize);      
               g.setColor(borderColor);
               g.drawRect(c*squareSize, r*squareSize, squareSize, squareSize);
               
               //draw value if this isn't a barrier (not -1 value)
               if (pathGrid[c][r]<=-1)
                   g.setColor(Color.orange);
               else
                   g.setColor(Color.magenta);
               
               g.drawString(""+pathGrid[c][r], c*squareSize+(int)(squareSize/4.0), r*squareSize+15);
          
           }
       }
    }

    public void drawSolutions(){ 
        //clear the area, draw white background
        ibg.clearRect(0, 0, gridSize,gridSize);
        ibg.setColor(Color.white);
        ibg.fillRect(0, 0, gridSize, gridSize);
        
        //draws individual squares (pass this method the frames graphics object
        drawPathSquares(ibg);
        drawSolutionSquare(targetCol, targetRow, ibg);
        //draws a black border around edge of grid
        ibg.setColor(Color.black);
        ibg.drawRect(0,0,gridSize,gridSize);
        
        //all done drawing your stuff onto the image buffer?
        //get the frame's graphics object and draw our image buffer onto the frame
        Graphics g = this.getGraphics();
        g.drawImage(ib,offSet,offSet,this);
    }    
    
    public void drawSolutionSquare(int currentCol, int currentRow, Graphics g){
       //if we have reached homeCol,homeRow then stop
       if (currentRow==homeRow && currentCol==homeCol)
           return;
       
       //highlight this square if not target
       if (!(currentRow==targetRow && currentCol==targetCol)) {
            g.setColor(Color.cyan);
            g.fillRect(currentCol*squareSize, currentRow*squareSize, squareSize, squareSize); 
            g.setColor(borderColor);
            g.drawRect(currentCol*squareSize, currentRow*squareSize, squareSize, squareSize);
            g.setColor(Color.black);
            g.drawString(""+pathGrid[currentCol][currentRow], currentCol*squareSize+(int)(squareSize/4.0), currentRow*squareSize+15);
       }
       
       //check each neighbor.  If its value is one less than this square then ask that square to draw out and continue
        int val=pathGrid[currentCol][currentRow];
        for(int cx=-1; cx<=1; cx+=1) 
          for(int rx=-1; rx<=1; rx+=1){
              if ( valid(currentCol+cx,currentRow+rx) ) {
                  int val2=pathGrid[currentCol+cx][currentRow+rx];
                  if ( val2==(val-1) ) {
                      drawSolutionSquare(currentCol+cx,currentRow+rx,g);
                      return;
                  }
                  
              }
          }
    }   
    
    
    /*
               g.setColor(Color.cyan);
               g.fillRect(c*squareSize, r*squareSize, squareSize, squareSize);      
               g.setColor(borderColor);
               g.drawRect(c*squareSize, r*squareSize, squareSize, squareSize);
             
               //draw number over?
               g.drawString(""+pathGrid[c][r], c*squareSize+(int)(squareSize/4.0), r*squareSize+15);
    */
    
    
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
        jButtonStep = new javax.swing.JButton();
        jButtonCopy = new javax.swing.JButton();
        jButtonPaste = new javax.swing.JButton();
        jButtonRun = new javax.swing.JButton();
        jSliderDelay = new javax.swing.JSlider();
        jPanel1 = new javax.swing.JPanel();
        buttonResetPathGrid = new javax.swing.JButton();
        buttonSolvePathStep = new javax.swing.JButton();
        buttonDrawPathGrid = new javax.swing.JButton();
        buttonTest = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
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
        buttonBlack.setText("BLACK (BARRIER)");
        buttonBlack.setMaximumSize(new java.awt.Dimension(50, 30));
        buttonBlack.setMinimumSize(new java.awt.Dimension(75, 0));
        buttonBlack.setPreferredSize(new java.awt.Dimension(50, 20));
        buttonBlack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBlackActionPerformed(evt);
            }
        });

        buttonWhite.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        buttonWhite.setText("WHITE (NO BARRIER)");
        buttonWhite.setMaximumSize(new java.awt.Dimension(50, 30));
        buttonWhite.setMinimumSize(new java.awt.Dimension(75, 0));
        buttonWhite.setPreferredSize(new java.awt.Dimension(50, 20));
        buttonWhite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonWhiteActionPerformed(evt);
            }
        });

        jButtonStep.setText("STEP");
        jButtonStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStepActionPerformed(evt);
            }
        });

        jButtonCopy.setText("Copy");

        jButtonPaste.setText("Paste");

        jButtonRun.setText("Run");
        jButtonRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRunActionPerformed(evt);
            }
        });

        jSliderDelay.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jSliderDelay.setMajorTickSpacing(500);
        jSliderDelay.setMaximum(2000);
        jSliderDelay.setMinorTickSpacing(500);
        jSliderDelay.setPaintLabels(true);
        jSliderDelay.setPaintTicks(true);
        jSliderDelay.setSnapToTicks(true);
        jSliderDelay.setValue(500);
        jSliderDelay.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderDelayStateChanged(evt);
            }
        });

        buttonResetPathGrid.setText("Reset PathGrid");
        buttonResetPathGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonResetPathGridActionPerformed(evt);
            }
        });

        buttonSolvePathStep.setText("Solve Path Step");
        buttonSolvePathStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSolvePathStepActionPerformed(evt);
            }
        });

        buttonDrawPathGrid.setText("Draw Path Grid");
        buttonDrawPathGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDrawPathGridActionPerformed(evt);
            }
        });

        buttonTest.setText("test");
        buttonTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTestActionPerformed(evt);
            }
        });

        jButton5.setText("jButton5");

        jButton6.setText("jButton6");

        jButton7.setText("jButton7");

        jButton8.setText("jButton8");

        jButton9.setText("jButton9");

        jButton10.setText("jButton10");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("jButton11");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("jButton12");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonResetPathGrid, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                    .addComponent(buttonSolvePathStep, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonDrawPathGrid, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonTest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 10, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonDrawPathGrid, buttonResetPathGrid, buttonSolvePathStep, buttonTest, jButton10, jButton11, jButton12, jButton5, jButton6, jButton7, jButton8, jButton9});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(buttonResetPathGrid)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonSolvePathStep)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonDrawPathGrid)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonTest)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton12)
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
                                    .addComponent(jButtonStep, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(buttonWhite, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButtonCopy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButtonPaste, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(buttonOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButtonRun, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(28, 28, 28)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(156, 156, 156)
                        .addComponent(jLabel1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonBlack, buttonClear, buttonOpen, buttonRefresh, buttonSave, buttonWhite, jButtonCopy, jButtonPaste, jButtonRun, jButtonStep});

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
                        .addComponent(jButtonCopy)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonPaste)
                        .addGap(20, 20, 20)
                        .addComponent(buttonBlack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonWhite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(jButtonStep)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonRun)))
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
      System.out.println("Filename: " + filename);
      FC.close();
      
      FileOutput FO = new FileOutput(filename,"w");
    
      for(int r=0; r<gridCount; r++) {
         for(int c=0; c<gridCount; c++) {
             int num = grid[c][r];
             if (num<10 && num >=0)
                 FO.print("0"+num);
             else if (num==-1)
                 FO.print("-1");
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
              grid[c][r]=value;                                  //put in grid
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
        penColor=-1;    //make a wall!!!
    }//GEN-LAST:event_buttonBlackActionPerformed

    private void buttonWhiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonWhiteActionPerformed
        penColor=0;
    }//GEN-LAST:event_buttonWhiteActionPerformed

    public boolean isAlive(int c, int r) {
        //if cell is in grid and alive, return true
        //outside grid or not alive, return false
        return(true);
    }
    
    private void jButtonStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStepActionPerformed
        step();
    }//GEN-LAST:event_jButtonStepActionPerformed

    public int countNeighbors(int c, int r){
       return(0);
    }
    
    public void step() {
       solvePathStep();
        drawPathGrid();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        //this event is called by the timer...
        //System.out.println("Calling Step...");
        //System.out.println(e.getSource());
        step();
    }
    
    private void jButtonRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRunActionPerformed
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
            jButtonRun.setText("Pause...");
        }
        else { //turn timer off
            active=false;
            timmy.stop();
            jButtonRun.setText("Run...");
        }
    }//GEN-LAST:event_jButtonRunActionPerformed

    private void jSliderDelayStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderDelayStateChanged
        // TODO add your handling code here:
        stepDelay=jSliderDelay.getValue();
        if (stepDelay==0) {
            stepDelay=1;
        }
        if (timmy!=null)
            timmy.setDelay(stepDelay);
    }//GEN-LAST:event_jSliderDelayStateChanged

    private void buttonResetPathGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonResetPathGridActionPerformed
        //make pathGrid all 0
        for(int r=0; r<gridCount; r++)
           for(int c=0; c<gridCount; c++) 
                 pathGrid[c][r]=0;
        
        //copy the barriers into the pathGrid
         for(int r=0; r<gridCount; r++){
           for(int c=0; c<gridCount; c++) {
               int val=grid[c][r];
               if (val==-1)
                   pathGrid[c][r]=-1;    //-1 to indicate barrier
           }
         }
         
        //set home to 1,1
        homeCol=1; homeRow=1;targetCol=11; targetRow=11;
        pathGrid[homeCol][homeRow]=1;
        //no need to set target... just remember it
        drawPathGrid();
    }//GEN-LAST:event_buttonResetPathGridActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        for(int c=1; c<gridCount-1; c++) {
            for(int r=1; r<gridCount-1; r++) {
                if (grid[c][r]==1) {
                    grid[c+1][r]=1;
                }
            }
        }
        
        draw();
    }//GEN-LAST:event_jButton11ActionPerformed

    public void stuff(int x) {
        if (x>0) {
            System.out.println(x);
            stuff(x-1);
        }
        System.out.print(x);
    }
    
    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
         jMode=true;
         System.out.println("jMode true!");
        
        /* int[][] temp = new int[gridCount][gridCount];
        for(int c=1; c<gridCount-1; c++) {
            for(int r=1; r<gridCount-1; r++) {
                if (grid[c][r]==1) {
                    temp[c][r]=1;
                    temp[c+1][r]=1;
                }
            }
        }
        
        grid=temp;
        draw();
        */
    }//GEN-LAST:event_jButton10ActionPerformed

    public boolean valid(int c, int r) {
        if (c>=0 && c<gridCount && r>=0 && r<gridCount)
            return(true);
        return(false);
    }
 
   public int findMinNeighbor(int c, int r) {
      mins.clear();
      for(int cx=-1; cx<=1; cx+=1) 
          for(int rx=-1; rx<=1; rx+=1)
              if ( valid(c+cx,r+rx) && pathGrid[c+cx][r+rx]>0 )
                  mins.add(pathGrid[c+cx][r+rx]);
       
      int min=0;
      if (mins.size()>0) {
         min=mins.get(0);
          for(int x:mins)
             if (x<min)
                 min=x;
      }  
      
      return(min);
   }
   
    public void solvePathStep() {
        //create a temp matrix to find the new state of the pathGrid
        int[][] temp=new int[gridCount][gridCount];  
        //start putting values into temp base on path solving rules check neighbors and see what number to acquire.
        for(int r=0; r<gridCount; r++) {
            for(int c=0; c<gridCount;c++) {
                int val=pathGrid[c][r];
                if (val==-1) {
                    temp[c][r]=-1;
                }
                else if (val==0) {    //if not a barrier, try to change neighbors to lower value
                    int min=findMinNeighbor(c,r);
                    if (min!=0)
                        temp[c][r]=findMinNeighbor(c,r)+1;
                }
                else if (val>0 ) {
                    int min=findMinNeighbor(c,r)+1;
                    if (min<val)
                        temp[c][r]=min;
                    else
                        temp[c][r]=val;
                }//else
            }//c
        }//r
          
        //copy all these changes into the grid
        pathGrid=temp;
         
         drawPathGrid();
    }//method
    
    
    private void buttonSolvePathStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSolvePathStepActionPerformed
        solvePathStep();
        drawPathGrid();
        checkIfSolved(); 
    }//GEN-LAST:event_buttonSolvePathStepActionPerformed

    public void checkIfSolved() {
        //check for solution found
        if (pathGrid[targetCol][targetRow]>0) {
            textInfo.setText("Path Found!   " + pathGrid[targetCol][targetRow] + " steps to complete path!");
            drawSolutions();
        }
        else {
            textInfo.setText("Still working on path...");
        }
    }
    
   
    
    private void buttonDrawPathGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDrawPathGridActionPerformed
        drawPathGrid();
    }//GEN-LAST:event_buttonDrawPathGridActionPerformed

    private void buttonTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTestActionPerformed
       System.out.println(findMinNeighbor(2,2));
       
    }//GEN-LAST:event_buttonTestActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PathFinder().setVisible(true);
            }
        });
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonBlack;
    private javax.swing.JButton buttonClear;
    private javax.swing.JButton buttonDrawPathGrid;
    private javax.swing.JButton buttonOpen;
    private javax.swing.JButton buttonRefresh;
    private javax.swing.JButton buttonResetPathGrid;
    private javax.swing.JButton buttonSave;
    private javax.swing.JButton buttonSolvePathStep;
    private javax.swing.JButton buttonTest;
    private javax.swing.JButton buttonWhite;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JButton jButtonCopy;
    private javax.swing.JButton jButtonPaste;
    private javax.swing.JButton jButtonRun;
    private javax.swing.JButton jButtonStep;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSlider jSliderDelay;
    private javax.swing.JTextField textInfo;
    // End of variables declaration//GEN-END:variables

} //end of class