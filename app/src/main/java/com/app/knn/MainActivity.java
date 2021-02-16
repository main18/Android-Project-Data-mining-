package com.app.knn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    EditText age = null;
    RadioGroup sexGroup = null;
    RadioGroup bpGroup = null;
    RadioGroup bcGroup = null;
    EditText NaValue = null;
    EditText kValue = null;
    Button predictBtn = null;
    Button raz = null;
    TextView result = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        // Initializing
        age = (EditText)findViewById(R.id.age);
        sexGroup = (RadioGroup)findViewById(R.id.SexGroup);
        bpGroup = (RadioGroup)findViewById(R.id.BPGroup);;
        bcGroup = (RadioGroup)findViewById(R.id.BCGroup);
        NaValue = (EditText)findViewById(R.id.Na);
        kValue = (EditText)findViewById(R.id.K);
        predictBtn = (Button)findViewById(R.id.prediction);
        raz = (Button)findViewById(R.id.raz);
        result = (TextView)findViewById(R.id.result);


        // lISTENING TO CLICKS AND EXECUTION A FUNCTION TO PRODUCE RESULTS
        predictBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int ageValue = Integer.parseInt(age.getText().toString());

                // reading radio button value
                int selectedId1 = sexGroup.getCheckedRadioButtonId();
                RadioButton sexBtn = (RadioButton)findViewById(selectedId1);
                String sexValue = sexBtn.getText().toString();

                //reading radio button value
                int selectedId2 = bpGroup.getCheckedRadioButtonId();
                RadioButton bpBtn = (RadioButton)findViewById(selectedId2);
                String bpValue = bpBtn.getText().toString();

                //reading radio button value
                int selectedId3 = bcGroup.getCheckedRadioButtonId();
                RadioButton bcBtn = (RadioButton)findViewById(selectedId3);
                String bcValue = bcBtn.getText().toString();

                double naValue = parseDouble(NaValue.getText().toString());
                double Kvalue = parseDouble(kValue.getText().toString());

                TextView resultView = (TextView)findViewById(R.id.result);

                // executer la fonction algorithme
                algorithme(ageValue, sexValue, bpValue, bcValue, naValue, Kvalue, resultView);

            }
        });

        raz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                age.getText().clear();
                NaValue.getText().clear();
                kValue.getText().clear();
                result.setText("");
                sexGroup.clearCheck();
                bcGroup.clearCheck();
                bpGroup.clearCheck();
            }
        });


    }

    // List qui va contenir l'ensemble des données
    private List<Data> DataSamples = new ArrayList<>();

    // List qui va contenir les distances calculés pour pouvoir décider le type de drug
    private List<CompareObject> compareTable = new ArrayList<CompareObject>();

    private void algorithme(int age, String sexValue, String bp, String bc, double naValue, double Kvalue, TextView result){
        // Lecture des données d'apprentissage depuis le fichier csv
        InputStream is = getResources().openRawResource(R.raw.drugs);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";

        try{
            while((line = reader.readLine()) != null){

                // Split data By ','
                String[] tokens = line.split(",");

                Data sample = new Data();
                sample.setAge(parseInt(tokens[0]));
                sample.setSex(tokens[1]);
                sample.setBP(tokens[2]);
                sample.setCholesterol(tokens[3]);
                sample.setNa(parseDouble(tokens[4]));
                sample.setK(parseDouble(tokens[5]));
                sample.setDrug(tokens[6]);

                // Creating the persons that we're going to compare with the data from above
                Individu p = new Individu(age, sexValue, bp, bc, naValue, Kvalue);

                // Calculating the distance
                // sqrt((age-age)2 + sex( 1 | 0)2 + BP(high 0.5 normal 0.5 low )2 + CH ( 1 | 0 )2+(Na-Na)2+(K-K)2
                int ageCalc = sample.getAge() - p.getAge();
                int sex = sample.getSex().equals(p.getSex()) ? 0 : 1;

                // high --0.5-> Normal --0.5-> Low
                double BP = 0;
                if(sample.getBP().equals(p.getBP())){
                    BP = 0;
                }else {
                    if((sample.getBP().equals("HIGH") && p.getBP().equals("NORMAL")) ||
                            (sample.getBP().equals("NORMAL") && p.getBP().equals("HIGH"))){
                        BP = 0.5;
                    }
                    else if((sample.getBP().equals("HIGH") && p.getBP().equals("LOW")) ||
                            (sample.getBP().equals("LOW") && p.getBP().equals("HIGH"))){
                        BP = 1;
                    }
                    else if((sample.getBP().equals("NORMAL") && p.getBP().equals("LOW")) ||
                            (sample.getBP().equals("LOW") && p.getBP().equals("NORMAL"))){
                        BP = 1;
                    }
                }

                // High --1--> Normal
                int CH = sample.getCholesterol().equals(p.getCholesterol()) ? 0 : 1;
                double Na = sample.getNa() - p.getNa();
                double K = sample.getK() - p.getK();

                // La formule finale
                double formule = Math.sqrt(Math.pow(ageCalc,2) + Math.pow(sex,2) + Math.pow(BP,2)
                        + Math.pow(CH,2) + Math.pow(Na,2) + Math.pow(K,2));

                // l'ajout des valeurs calculés avec leurs drugs
                compareTable.add(new CompareObject(formule, sample.getDrug()));

                // rénitialiser la variable formule à la fin de chaque itération
                formule = 0;
            }

            Log.d("array => ", "CampareArray => " + this.compareTable.size());


            // Extracting the minimaum three and seeing their drugs to set the individul's drug to the correct one
            // le premier minimaum
            CompareObject v1 = new CompareObject(Integer.MAX_VALUE, "");
            // le deuxieme minimaum
            CompareObject v2 = new CompareObject(Integer.MAX_VALUE, "");
            // le troisième minimaum
            CompareObject v3 = new CompareObject(Integer.MAX_VALUE, "");
            for(int i=0;i<this.compareTable.size();i++){
                int max = 0;
                if(this.compareTable.get(i).getValue() < v1.getValue()){
                    v3.setValue(v2.getValue());
                    v2.setValue(v1.getValue());
                    v1.setValue(this.compareTable.get(i).getValue());
                    v1.setDrug(this.compareTable.get(i).getDrug());
                }else if(this.compareTable.get(i).getValue() < v2.getValue() ){
                    v3.setValue(v2.getValue());
                    v2.setValue(this.compareTable.get(i).getValue());
                    v2.setDrug(this.compareTable.get(i).getDrug());
                }else if(this.compareTable.get(i).getValue() < v3.getValue()){
                    v3.setValue(this.compareTable.get(i).getValue());
                    v3.setDrug(this.compareTable.get(i).getDrug());
                }
            }

            // objet Individu final
            Individu pf = new Individu(age, sexValue, bp, bc, naValue, Kvalue);

            // affectation des drugs aux individus appropriés
            if(v1.getDrug().equals(v2.getDrug())){
                pf.setDrug(v1.getDrug());
            }else if(v2.getDrug().equals(v3.getDrug())){
                pf.setDrug(v2.getDrug());
            }
            else if(v3.getDrug().equals(v1.getDrug())){
                pf.setDrug(v3.getDrug());
            }
            else{
                pf.setDrug("erreur : les données source sont erronées");
            }
            result.setText(pf.getDrug());
            this.compareTable.clear();
        }catch(IOException e){
            Log.wtf("MyActivity", "Erreur lors de la lecture des données d'appretissage" + line, e);
        }

    }

}



