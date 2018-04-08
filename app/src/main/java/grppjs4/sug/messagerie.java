package grppjs4.sug;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import grppjs4.sug.Adapters.TchatAdapter;
import grppjs4.sug.Entities.Message;
import grppjs4.sug.Entities.User;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link messagerie.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link messagerie#newInstance} factory method to
 * create an instance of this fragment.
 */
public class messagerie extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private EditText etMessage;
    private ImageButton sendButton,imageButton;
    private RecyclerView recycler;
    private ProgressBar imageLoader;


    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;


    private FirebaseAuth.AuthStateListener authStateListener;
    private ChildEventListener childEventListener;

    private UploadTask uploadTask;

    private SharedPreferences prefs;
    private String username;
    private String userId;
    // private com.example.otmane.pjs4tchat.Entities.Message messages;

    private TchatAdapter adapter;

    private static final String TAG = "Tchat";

    private static final int SELECT_PHOTO =1;
    private OnFragmentInteractionListener mListener;
    private ImageButton btnsend;
    public messagerie() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment messagerie.
     */
    // TODO: Rename and change types and number of parameters
    public static messagerie newInstance(String param1, String param2) {
        messagerie fragment = new messagerie();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View r = inflater.inflate(R.layout.fragment_messagerie, container, false);


        initViews(r);
        initFirebase();

        prefs = getContext().getSharedPreferences("save",MODE_PRIVATE);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user !=null){

                    attachChildListener();
                    username =prefs.getString("PSEUDO",null);
                    userId=user.getUid();

                    adapter.setUser(user);

                }else{
                   // startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                  //  finish();
                }
            }
        };

        btnsend=(ImageButton)r.findViewById(R.id.sendButton);
        btnsend.setOnClickListener(actionsend);
        return r;
    }


    View.OnClickListener actionsend = new View.OnClickListener() {
        public void onClick(View v) {
            sendMessage(null);
        }
    };


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

    private void attachChildListener() {

        if(childEventListener ==null){
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.w(TAG, "onChildAdded: " );


                    // com.example.otmane.pjs4tchat.Entities.Message message = dataSnapshot.getValue(com.example.otmane.pjs4tchat.Entities.Message.class);
                    //message.setUid(dataSnapshot.getKey());
                    Message message = dataSnapshot.getValue(Message.class);
                    message.setUid(dataSnapshot.getKey());
                    adapter.addMessage(message);
                    recycler.scrollToPosition(adapter.getItemCount()-1);


                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                Message message = dataSnapshot.getValue(Message.class);
                    message.setUid(dataSnapshot.getKey());
                    adapter.deleteMessage(message);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mRef.child(Constants.MESSAGES_DB).limitToLast(100).addChildEventListener(childEventListener);

        }


    }
    private void detachChildListener(){
        if(childEventListener!=null){
            mRef.child(Constants.MESSAGES_DB).removeEventListener(childEventListener);
            childEventListener= null;

        }
    }


    private void initViews(View v){
        etMessage= v.findViewById(R.id.etMessage);
        sendButton= v.findViewById(R.id.sendButton);
        imageButton=v.findViewById(R.id.imageButton);
        recycler=v.findViewById(R.id.recycler);
        //imageLoader=(ProgressBar) v.findViewById(R.id.imageLoader);

        LinearLayoutManager manager = new LinearLayoutManager(this.getContext());
        manager.setStackFromEnd(true);
        recycler.setLayoutManager(manager);

        ArrayList<Message> messages = new ArrayList<>();

        adapter = new TchatAdapter(messages);
        recycler.setAdapter(adapter);

        etMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    sendMessage(null);
                    InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    return true;

                }
                return false;
            }

        });
    }


    private void ClearOnLogout(){

        FirebaseUser user = mAuth.getCurrentUser();
        if(user !=null){
            mRef.child(Constants.USER_DB).child(user.getUid()).removeValue();
            mRef.child(Constants.USERNAME_DB).child(username).removeValue();
            prefs.edit().remove("PSEUDO").apply();
            adapter.clearMessage();
            detachChildListener();
            //startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            //finish();


        }

    }




    private void sendMessage(String imageUrl) {
        Message message =null;

        if(imageUrl==null){

            String content = etMessage.getText().toString();
            if(!TextUtils.isEmpty(content)){

                message = new Message(username,userId,content,null);


            }

            else{
                message =new Message(username,userId,null,imageUrl);
            }

            mRef.child(Constants.MESSAGES_DB).push().setValue(message);
            etMessage.setText("");

        }





    }



    private void initFirebase(){

        mAuth =FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        mStorage  = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl(Constants.STORAGE_PATH).child(Constants.STORAGE_REF);


    }


    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(authStateListener); //firebaseAuth is of class FirebaseAuth
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }
    }
