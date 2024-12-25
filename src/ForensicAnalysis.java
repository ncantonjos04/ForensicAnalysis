/**
 * This class represents a forensic analysis system that manages DNA data using
 * BSTs.
 * Contains methods to create, read, update, delete, and flag profiles.
 * 
 */
public class ForensicAnalysis {

    private TreeNode treeRoot;            // BST's root
    private String firstUnknownSequence;
    private String secondUnknownSequence;

    public ForensicAnalysis () {
        treeRoot = null;
        firstUnknownSequence = null;
        secondUnknownSequence = null;
    }

    /**
     * Builds a simplified forensic analysis database as a BST and populates unknown sequences.
     * The input file is formatted as follows:
     * 1. one line containing the number of people in the database, say p
     * 2. one line containing first unknown sequence
     * 3. one line containing second unknown sequence
     * 2. for each person (p), this method:
     * - reads the person's name
     * - calls buildSingleProfile to return a single profile.
     * - calls insertPerson on the profile built to insert into BST.
     *      Use the BST insertion algorithm from class to insert.
     * 
     * 
     * @param filename the name of the file to read from
     */
    public void buildTree(String filename) {

        StdIn.setFile(filename); 

        // Reads unknown sequences
        String sequence1 = StdIn.readLine();
        firstUnknownSequence = sequence1;
        String sequence2 = StdIn.readLine();
        secondUnknownSequence = sequence2;
        
        int numberOfPeople = Integer.parseInt(StdIn.readLine()); 

        for (int i = 0; i < numberOfPeople; i++) {
            // Reads name, count of STRs
            String fname = StdIn.readString();
            String lname = StdIn.readString();
            String fullName = lname + ", " + fname;
            // Calls buildSingleProfile to create
            Profile profileToAdd = createSingleProfile();
            // Calls insertPerson on that profile: inserts a key-value pair (name, profile)
            insertPerson(fullName, profileToAdd);
        }
    }

    /** 
     * Reads ONE profile from input file and returns a new Profile.
    */
    public Profile createSingleProfile() {


        int s = StdIn.readInt();
        STR[] strs = new STR[s];
        for (int i = 0; i < strs.length; i++)
        {
            STR currentStr = new STR(StdIn.readString(),StdIn.readInt());
            strs[i] = currentStr;
        }
        Profile profile = new Profile(strs);
        return profile; // update this line
    }

    /**
     * Inserts a node with a new (key, value) pair into
     * the binary search tree rooted at treeRoot.
     * 
     * Names are the keys, Profiles are the values.
     * 
     * @param newProfile the profile to be inserted
     */
    public void insertPerson(String name, Profile newProfile) {

        treeRoot = insertPerson(treeRoot, name, newProfile);
        


    }
    private TreeNode insertPerson(TreeNode x, String name, Profile newProfile)
    {
        if (x==null)
            return new TreeNode(name,newProfile,null,null);
        int cmp = name.compareTo(x.getName());
        if (cmp<0)
            x.setLeft(insertPerson(x.getLeft(),name,newProfile));
        else if (cmp >0)
            x.setRight(insertPerson(x.getRight(),name,newProfile));
        else if (cmp == 0)
            x.setProfile(newProfile);
        return x;
    }

    /**
     * Finds the number of profiles in the BST whose interest status matches
     * isOfInterest.
     *
     * @param isOfInterest the search mode: whether we are searching for unmarked or
     *                     marked profiles. true if yes, false otherwise
     * @return the number of profiles according to the search mode marked
     */
    private static int numOfProfiles(TreeNode x)
    {
        if (x==null)
        {
            return 0;
        }
        int selena = 0;
        if(x.getProfile().getMarkedStatus()==true)
            selena++;
        selena+=numOfProfiles(x.getLeft());
        selena+= numOfProfiles(x.getRight());
        
        return selena;
       
        
    }
    private static int numOfProfiless(TreeNode x)
    {
        if (x==null)
        {
            return 0;
        }
        int koocha = 0;
        if(x.getProfile().getMarkedStatus() == false)
            koocha++;
        koocha+=numOfProfiless(x.getLeft());
        koocha+=numOfProfiless(x.getRight());
        
        return koocha;
       
        
    }
    

    public int getMatchingProfileCount(boolean isOfInterest) {
        

        TreeNode x = treeRoot;
        int match = numOfProfiles(x);
        TreeNode y = treeRoot;
        int dotcom = numOfProfiless(y);
        
        if (isOfInterest== true) 
            return match;
        else
            return dotcom; 
        // update this line
    }

    /**
     * Helper method that counts the # of STR occurrences in a sequence.
     * 
     * @param sequence the sequence to search
     * @param STR      the STR to count occurrences of
     * @return the number of times STR appears in sequence
     */
    private int numberOfOccurrences(String sequence, String STR) {
        
        
        int repeats = 0;
        // STRs can't be greater than a sequence
        if (STR.length() > sequence.length())
            return 0;
        
            // indexOf returns the first index of STR in sequence, -1 if not found
        int lastOccurrence = sequence.indexOf(STR);
        
        while (lastOccurrence != -1) {
            repeats++;
            // Move start index beyond the last found occurrence
            lastOccurrence = sequence.indexOf(STR, lastOccurrence + STR.length());
        }
        return repeats;
    }

    /**
     * Traverses the BST at treeRoot to mark profiles if:
     * - For each STR in profile STRs: at least half of STR occurrences match (round
     * UP)
     * - If occurrences THROUGHOUT DNA (first + second sequence combined) matches
     * occurrences, add a match
     */
    private void traverse (TreeNode x)
    {
        if (x == null)
            return;
        traverse(x.getLeft());
        traverse(x.getRight());
        int sameSTRcount = 0;
        for (int i = 0; i <x.getProfile().getStrs().length;i++)
        {
            if(x.getProfile().getStrs()[i].getOccurrences()==numberOfOccurrences(firstUnknownSequence,x.getProfile().getStrs()[i].getStrString())+numberOfOccurrences(secondUnknownSequence,x.getProfile().getStrs()[i].getStrString())) 
                sameSTRcount++;
        }
        if (sameSTRcount >= (Math.ceil(x.getProfile().getStrs().length)/2))
        x.getProfile().setInterestStatus(true);
        
    }
    public void flagProfilesOfInterest() {


        TreeNode x = treeRoot;
        traverse(x);
       
    }

    /**
     * Uses a level-order traversal to populate an array of unmarked Strings representing unmarked people's names.
     * 
     * @return the array of unmarked people
     */

    public String[] getUnmarkedPeople() {


        String[] people = new String[getMatchingProfileCount((false))];
        if (treeRoot == null)
            return people;
        Queue<TreeNode> que = new Queue<>();
        TreeNode nood = treeRoot;
        que.enqueue(nood);
        //int index = 0;
        while (!que.isEmpty())
        {
            //nood
            nood = que.dequeue();
         //   if (!nood.getProfile().getMarkedStatus()) //visit
           //     people[index++] = nood.getName();
            if (nood.getLeft()!=null)
                que.enqueue(nood.getLeft());
            if (nood.getRight()!=null)
                que.enqueue(nood.getRight());
        }
        

        return people; // update this line
    }

    /**
     * Removes a SINGLE node from the BST rooted at treeRoot, given a full name (Last, First)
     * 
     * If a profile containing fullName doesn't exist, do nothing.
     * 
     * @param fullName the full name of the person to delete
     */
    private TreeNode min(TreeNode nood)
    {
        if (nood.getLeft() == null)
            return nood;
        else 
            return (min(nood.getLeft()));
    }
    private TreeNode deleteMin(TreeNode nood)
    {
        if (nood.getLeft() == null)
            return nood.getRight();
        nood.setLeft(deleteMin(nood.getLeft()));
        return nood;
    }
    private TreeNode delete(TreeNode nood, String name)
    {
        if (nood == null)
            return null;
        int cmp = name.compareTo(nood.getName());
        if (cmp <0)
            nood.setLeft(delete(nood.getLeft(),name));
        else if (cmp > 0)
            nood.setRight(delete(nood.getRight(),name));
        else
        {
            if (nood.getRight()==null)
                return nood.getLeft();
            else if (nood.getLeft()==null)
                return nood.getRight();

            TreeNode newNode = nood;
            nood = min(newNode.getRight());
            nood.setRight(deleteMin(newNode.getRight()));
            nood.setLeft(newNode.getLeft());
        }
        return nood;
    }
    public void removePerson(String fullName) {
       treeRoot = delete(treeRoot,fullName);
    }

    /**
     * Clean up the tree by using previously written methods to remove unmarked
     * profiles.
     * Requires the use of getUnmarkedPeople and removePerson.
     */
    public void cleanupTree() {
        String[] unmarked = new String[getUnmarkedPeople().length];
        for (int x = 0; x<unmarked.length;x++)
        {
            unmarked[x]=getUnmarkedPeople()[x];
        }
        for (int i = 0; i<unmarked.length; i++)
        {
            removePerson(unmarked[i]);
        }
    }

    /**
     * Gets the root of the binary search tree.
     *
     * @return The root of the binary search tree.
     */
    public TreeNode getTreeRoot() {
        return treeRoot;
    }

    /**
     * Sets the root of the binary search tree.
     *
     * @param newRoot The new root of the binary search tree.
     */
    public void setTreeRoot(TreeNode newRoot) {
        treeRoot = newRoot;
    }

    /**
     * Gets the first unknown sequence.
     * 
     * @return the first unknown sequence.
     */
    public String getFirstUnknownSequence() {
        return firstUnknownSequence;
    }

    /**
     * Sets the first unknown sequence.
     * 
     * @param newFirst the value to set.
     */
    public void setFirstUnknownSequence(String newFirst) {
        firstUnknownSequence = newFirst;
    }

    /**
     * Gets the second unknown sequence.
     * 
     * @return the second unknown sequence.
     */
    public String getSecondUnknownSequence() {
        return secondUnknownSequence;
    }

    /**
     * Sets the second unknown sequence.
     * 
     * @param newSecond the value to set.
     */
    public void setSecondUnknownSequence(String newSecond) {
        secondUnknownSequence = newSecond;
    }

}
