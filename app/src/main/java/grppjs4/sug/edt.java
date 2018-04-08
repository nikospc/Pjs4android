package grppjs4.sug;

import android.content.Context;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link edt.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link edt#newInstance} factory method to
 * create an instance of this fragment.
 */
public class edt extends Fragment implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;


    private OnFragmentInteractionListener mListener;
    public List<WeekViewEvent> listevents;

    public edt() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment edt.
     */
    // TODO: Rename and change types and number of parameters
    public static edt newInstance(String param1, String param2) {
        edt fragment = new edt();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    MonthLoader.MonthChangeListener mMonthChangeListener = new MonthLoader.MonthChangeListener() {
        @Override
        public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
            // Populate the week view with some events.
            //WeekViewEvent event = new WeekViewEvent(0,"Pweb",2018,2,17,13,0,2018,2,17,16,30);
            //List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
            //events.add(event);
            List<WeekViewEvent> events = ParcoursICS();
            // Return only the events that matches newYear and newMonth.
            List<WeekViewEvent> matchedEvents = new ArrayList<WeekViewEvent>();
            for (WeekViewEvent event : events) {
                if (eventMatches(event, newYear, newMonth)) {
                    matchedEvents.add(event);
                }
            }
            return matchedEvents;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // Get a reference for the week view in the layout.



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        //listevents=ParcoursICS();
        View myview=inflater.inflate(R.layout.fragment_edt, container, false);
        mWeekView = (WeekView) myview.findViewById(R.id.weekView);
        mWeekView.setMonthChangeListener(mMonthChangeListener);
        //Calendar rightNow = Calendar.getInstance();
        //int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
        mWeekView.goToHour(8);
        return  myview;
    }

    private void setupWeekview() {
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> events =ParcoursICS();
        return events;
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {

    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Checks if an event falls into a specific year and month.
     * @param event The event to check for.
     * @param year The year.
     * @param month The month.
     * @return True if the event matches the year and month.
     */
    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month-1) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }

    public List<WeekViewEvent> ParcoursICS() {
// Parcours le fichier ics et récupère les rendez-vous
        String ligne;
        String s="ii.ics";
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        Calendar startTime=Calendar.getInstance();
        Calendar endTime=Calendar.getInstance();
        String location=null;
        String resume = null;
        int anneed = 0,moisd=0,jourd=0,heured=0,mind=0,anneef=0,moisf=0,jourf=0,heuref=0,minf=0;
        int id=0;
        try {
            // ouverture du fichier texte (.ics)
            File file = new File(getActivity().getApplicationContext().getFilesDir(),s);
            BufferedReader planning = new BufferedReader(new FileReader(file));
            ligne = planning.readLine();
            Log.i("premiereligne", ligne);
            while(!ligne.equalsIgnoreCase("BEGIN:VEVENT")){
                ligne=planning.readLine();
                if(ligne.equalsIgnoreCase("END:VCALENDAR")){
                    return events;
                }
//                Log.i("lignesauté", ligne);
            }

            while (!ligne.equalsIgnoreCase("END:VCALENDAR") && ligne != null) {
                WeekViewEvent w = new WeekViewEvent();

                if (ligne.equalsIgnoreCase("BEGIN:VEVENT")) {
                    // Rendez-vous à prendre en compte
                    ligne = planning.readLine();
                    //Log.i("premiereboucle", ligne);
                    //ligne = planning.readLine();
                    //Log.i("b1", ligne);
                    while (!ligne.equalsIgnoreCase("END:VEVENT")) {

                        // récupération des informations utiles
                        if (ligne.contains("DTSTART:")) {
                            // date et heure de début du rendez-vous
                            anneed = Integer.parseInt(ligne.substring(8, 12));
                            moisd=Integer.parseInt(ligne.substring(12,14));
                            jourd=Integer.parseInt(ligne.substring(14,16));
                            heured=Integer.parseInt(ligne.substring(17,19));
                            mind=Integer.parseInt(ligne.substring(19,21));
                            /*startTime.set(Calendar.YEAR,Integer.parseInt(ligne.substring(8,12)));
                            startTime.set(Calendar.MONTH,Integer.parseInt(ligne.substring(12,14)));
                            startTime.set(Calendar.DAY_OF_MONTH,Integer.parseInt(ligne.substring(14,16)));
                            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ligne.substring(17,19)));
                            startTime.set(Calendar.MINUTE, Integer.parseInt(ligne.substring(19,21)));
                            //startTime.set(Calendar.SECOND, Integer.parseInt(ligne.substring(21,23)));*/
                            //w.setStartTime(cal);
                            //Log.i("date debut", w.getStartTime().toString());

                        }
                        else if (ligne.contains("DTEND:")) {
                            anneef=Integer.parseInt(ligne.substring(6,10));
                            moisf=Integer.parseInt(ligne.substring(10,12));
                            jourf=Integer.parseInt(ligne.substring(12,14));
                            heuref=Integer.parseInt(ligne.substring(15,17));
                            minf=Integer.parseInt(ligne.substring(17,19));
                            /*endTime.set(Calendar.YEAR,Integer.parseInt(ligne.substring(6,10)));
                            endTime.set(Calendar.MONTH,Integer.parseInt(ligne.substring(10,12)));
                            endTime.set(Calendar.DAY_OF_MONTH,Integer.parseInt(ligne.substring(12,14)));
                            endTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ligne.substring(15,17)));
                            endTime.set(Calendar.MINUTE, Integer.parseInt(ligne.substring(17,19)));
                           // endTime.set(Calendar.SECOND, Integer.parseInt(ligne.substring(20,21)));*/
                            //w.setEndTime(cal);*/
                            // Log.i("date debut", w.getEndTime().toString());
                            // date et heure de fin du rendez-vous

                        }
                        else if (ligne.contains("LOCATION:")) {
                            location=(ligne.substring(9));
                            //Log.i("lieu", w.getLocation());
                            // lieu du début du rendez-vous

                        }
                        else if (ligne.equalsIgnoreCase("DTSTAMP")) {
                            // date et heure de création du planning (utile pour savoir s'il a été modifié)

                        }
                        else if (ligne.equalsIgnoreCase("DESCRIPTION")) {
                            // description du rendez-vous (avec coordonnées PEQ)

                        }
                        else if (ligne.contains("SUMMARY:")) {
                            resume=(ligne.substring(8));
                            //Log.i("resume", w.getName());
                            // numéro de vol ou type de rendez-vous (hotel par exemple)

                        }
                        ligne = planning.readLine();
                        //Log.i("finif", ligne);

                    }
                    // traitement du rendez-vous : 1. mise en forme en fct des options, 2. création du rendez-vous dans l'agenda
                   // w=new WeekViewEvent(id,resume,location,startTime,endTime);
                    //w = new WeekViewEvent(0,"Pweb",2018,2,17,13,0,2018,2,17,16,30);
                    w= new WeekViewEvent(id,resume+" "+location,anneed,moisd,jourd,heured,mind,anneef,moisf,jourf,heuref,minf-1);
                    w.setColor(R.color.material_blue_grey_950);
                    events.add(w);
                    id++;
                }
                ligne = planning.readLine();
            }
            /*for(WeekViewEvent e:events){
                Log.i("events", e.toString());
            }*/
        }
        catch (FileNotFoundException e1) {
            // Erreur : le fichier n'existe pas
            Toast.makeText(getActivity().getApplicationContext(), "Erreur : fichier ics inexistant", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // Erreur : problème de lecture fichier
            Toast.makeText(getActivity().getApplicationContext(), "Erreur : problème de lecture du fichier ics", Toast.LENGTH_LONG).show();
        }
        return events;
    }

}

