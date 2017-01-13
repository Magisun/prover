package com.naveensundarg.shadow.prover.core;

import com.naveensundarg.shadow.prover.core.proof.Justification;
import com.naveensundarg.shadow.prover.core.proof.TrivialJustification;
import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.representations.value.Value;
import com.naveensundarg.shadow.prover.representations.value.Variable;
import com.naveensundarg.shadow.prover.utils.*;
import com.naveensundarg.shadow.prover.utils.Reader;
import org.armedbear.lisp.Interpreter;
import org.armedbear.lisp.LispObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * Created by naveensundarg on 12/4/16.
 */
public class SnarkWrapper implements Prover {


    private static AtomicBoolean local = new AtomicBoolean(true);

    public static boolean isLocal() {
        return local.get();
    }

    public static void setLocal(boolean local) {
        SnarkWrapper.local.set(local);
    }

    private final static Interpreter interpreter;
    static {


        if(local.get()) {

            interpreter = Interpreter.createInstance();
            LispObject result  = interpreter.eval("(load \"snark-20120808r02/snark-system.lisp\")");

            System.out.println(result);

            result = interpreter.eval("(make-snark-system)");


            System.out.println(result);

            result = interpreter.eval("(load \"snark-20120808r02/snark-interface.lisp\")");
            System.out.println(result);

        } else {
            interpreter = null;
        }


    }


    @Override
    public Optional<Justification> prove(Set<Formula> assumptions, Formula formula)  {




        String assumptionsListString = assumptions.stream().map(x-> x.toString()).reduce("'(", (x, y) -> x+ " " +y) +") ";
        String goalString = "'" +  formula.toString();

        assumptionsListString = assumptionsListString.replace("\n", "").replace("\r", "");
        goalString = goalString.replace("\n", "").replace("\r", "");

        String resultString = "";
        if(local.get()) {

            synchronized (interpreter) {


                LispObject result = interpreter.eval("(prove-from-axioms-yes-no " + assumptionsListString +  goalString+ " :verbose nil)");

               resultString = result.toString();
            }
        } else {

            String url = null;
            try {
                url = "http://localhost:8000/prove?assumptions=" + URLEncoder.encode(assumptionsListString, "UTF-8") + "&goal=" +URLEncoder.encode(goalString, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            URL proverURL = null;
            try {
                proverURL = new URL(url);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(
                        new InputStreamReader(proverURL.openStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String inputLine = null;
            try {
                while ((inputLine = in.readLine()) != null) {

                    resultString  = resultString + inputLine;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }



        }

        if(resultString.equals("YES")) {
            return Optional.of(new TrivialJustification(formula));
        }
        else {
            return Optional.empty();
        }



    }


    @Override
    public Optional<Value> proveAndGetBinding(Set<Formula> assumptions, Formula formula, Variable variable){




        String assumptionsListString = assumptions.stream().map(x-> x.toString()).reduce("'(", (x, y) -> x+ " " +y) +") ";
        String goalString = "'" +  formula.toString();

        assumptionsListString = assumptionsListString.replace("\n", "").replace("\r", "");
        goalString = goalString.replace("\n", "").replace("\r", "");

        String resultString = "";
        if(local.get()) {

            synchronized (interpreter) {


                LispObject result = interpreter.eval("(prove-from-axioms-and-get-answer " + assumptionsListString +  goalString+ " '" + variable.toString()+ " :verbose nil)");

               resultString = result.toString();
            }
        } else {

            String url = null;
            try {
                url = "http://localhost:8000/prove?assumptions=" + URLEncoder.encode(assumptionsListString, "UTF-8") + "&goal=" +URLEncoder.encode(goalString, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            URL proverURL = null;
            try {
                proverURL = new URL(url);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(
                        new InputStreamReader(proverURL.openStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String inputLine = null;
            try {
                while ((inputLine = in.readLine()) != null) {

                    resultString  = resultString + inputLine;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }



        }

        if(resultString.isEmpty()) {
            return Optional.empty();
        }
        else {


            try {
                return Optional.of(Reader.readLogicValueFromString(resultString));
            } catch (Reader.ParsingException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }



    }

}
