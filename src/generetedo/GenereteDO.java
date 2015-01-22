/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package generetedo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author vishnu-pt517
 */
public class GenereteDO {

    /**
     * @param args the command line arguments
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {

        File f = new File("src/Domain");
        GenereteDO main = new GenereteDO();
        ClassLoader classLoader = GenereteDO.class.getClassLoader();
        for (File names : f.listFiles()) {
            String className = names.getName().split("\\.")[0];
            Class c = classLoader.loadClass("Domain."+className);
            main.process(c, className);
        }
    }

    public void process(Class o, String name) throws FileNotFoundException, IOException {
        //String name = o.getClass().getName().split("\\.")[1];
        String tableName = this.parseIt(name);

        Field[] fields = o.getFields();
        String[] column = new String[fields.length];
        String[] type = new String[fields.length];
        String[] actual = new String[fields.length];

        System.out.println(fields.length);
        System.out.println(column.length);
        int i = 0;
        for (Field f : fields) {
            column[i++] = this.parseIt(f.getName());
            actual[i - 1] = f.getName();
            //System.out.println(column[i - 1]);
            String typ = f.getType().toString();
            if (typ.equals("long")) {
                type[i - 1] = "Long";
            } else if (typ.equals("class java.lang.String")) {
                type[i - 1] = "String";
            } else if (typ.equals("int")) {
                type[i - 1] = "Int";
            } else if (typ.equals("class java.sql.Date")) {
                type[i - 1] = "Date";
            } else if (typ.equals("class java.sql.Time")) {
                type[i - 1] = "Time";
            } else if (typ.equals("boolean")) {
                type[i - 1] = "Boolean";
            }
            System.out.println(type[i - 1]);
        }
        File f = new File("e:/out/" + name + "DO.java");
        f.createNewFile();
        System.out.println(f.getAbsolutePath());
        PrintWriter t = new PrintWriter(f);

        t.println("package do;\n"
                + "import java.sql.Connection;\n"
                + "import java.sql.PreparedStatement;\n"
                + "import java.sql.ResultSet;\n"
                + "import java.sql.SQLException;\n"
                + "import java.util.ArrayList;\n"
                + "import java.util.List;\n");
        t.println("public class " + name + "DO{");
        ///////////////////////////////////////////////////////////////////////////
        t.println("public void add(" + name + " obj) throws SQLException{");
        t.println("Connection con=util.ConnectionUtil.getConnection();");
        t.print("String q=\"insert into " + tableName + " (");
        boolean first = true;
        for (String c : column) {
            if (first) {
                t.print(c + " ");
                first = false;
            } else {
                t.print("," + c + " ");
            }
        }
        t.print(") values (");
        first = true;
        for (i = 0; i < column.length; i++) {
            if (first) {
                t.print("? ");
                first = false;
            } else {
                t.print(", ?");
            }
        }
        t.println(" )\";");
        t.println("PreparedStatement ps=con.prepareStatement(q);");

        for (i = 0; i < column.length; i++) {
            t.print("ps.set" + type[i] + "(" + (i + 1) + ",");
            t.println("obj." + actual[i] + ");");
        }
        t.println("ps.executeUpdate();");
        t.println("}");
        t.println("\n");
        //////////////////////////////////////////////////////////
        t.println("public void update(" + name + " obj) throws SQLException{");
        t.println("Connection con=util.ConnectionUtil.getConnection();");
        t.print("String q=\"update " + tableName + " set ");
        first = true;
        for (String c : column) {
            if (first) {
                t.print(c + "= ?");
                first = false;
            } else {
                t.print("," + c + "= ?");
            }
        }
        t.println(" where " + column[0] + " = ?;\";");
        t.println("PreparedStatement ps=con.prepareStatement(q);");

        for (i = 0; i < column.length; i++) {
            t.print("ps.set" + type[i] + "(" + (i + 1) + ",");
            t.println("obj." + actual[i] + ");");
        }
        t.println("ps.set" + type[0] + "(" + (column.length + 1) + ", obj." + actual[0] + ");");
        t.println("ps.executeUpdate();");
        t.println("}");
        //////////////////////////////////////////////////////////////////////////////
        t.println("public List<" + name + "> getAll(long id) throws SQLException{");
        t.println("Connection con=util.ConnectionUtil.getConnection();");
        t.println("String q=\"select * from " + tableName + " where " + column[0] + " =?;\";");
        t.println("PreparedStatement ps=con.prepareStatement(q);");
        t.println("ps.setLong(1,id);");
        t.println("ResultSet rs=ps.executeQuery();");
        t.println("List <" + name + "> out = new ArrayList<" + name + "> ();");
        t.println("while(rs.next()){");
        t.println(name + " obj=new " + name + "();");
        for (i = 0; i < column.length; i++) {
            t.println("obj." + actual[i] + " = rs.get" + type[i] + "(\"" + column[i] + "\");");
        }
        t.println("out.add(obj);");
        t.println("}");
        t.println("return out;");
        t.println("}");
        t.println("\n");
        //////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////
        t.println("public " + name + " get(long id) throws SQLException{");
        t.println("Connection con=util.ConnectionUtil.getConnection();");
        t.println("String q=\"select * from " + tableName + " where " + column[0] + " =?;\";");
        t.println("PreparedStatement ps=con.prepareStatement(q);");
        t.println("ps.setLong(1,id);");
        t.println("ResultSet rs=ps.executeQuery();");
        t.println(name + " obj=new " + name + "();");
        t.println("if(rs.next()) {");
        for (i = 0; i < column.length; i++) {
            t.println("obj." + actual[i] + " = rs.get" + type[i] + "(\"" + column[i] + "\");");
        }
        t.println("}");
        t.println("return obj;");
        t.println("}");
        t.println("\n");
        //////////////////////////////////////////////////////////

        t.println("");
        t.println("}");
        t.close();

    }

    private String parseIt(String name) {
        StringBuilder sb = new StringBuilder();
        name = name.trim();
        boolean first = true;
        for (Character a : name.toCharArray()) {
            if (first) {
                first = false;
                sb.append(Character.toLowerCase(a));
            } else if (java.lang.Character.isUpperCase(a)) {
                sb.append('_');
                sb.append(Character.toLowerCase(a));
            } else {
                sb.append(a);
            }
        }
        return sb.toString();
    }
}
