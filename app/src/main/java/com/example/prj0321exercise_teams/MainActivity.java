package com.example.prj0321exercise_teams;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prj0321exercise_teams.model.Project;
import com.example.prj0321exercise_teams.model.Team;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnFailureListener, OnSuccessListener, OnCompleteListener, ValueEventListener, ChildEventListener {
    EditText edTeamId, edTeamName, edProjectTitle, edProjectDesc, edProjectTotalStory, edMemberName1, edMemberName2, edMemberName3, edMemberName4;
    TextView tvSelectPhoto;
    ImageButton imPhoto;
    Button btnAdd, btnFind, btnList, btnQuit;

    DatabaseReference teamsDb;
    //    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference photoRef;

    Uri photoPath;
    String photoUrl;
    boolean isPhotoChosen = false;
    ProgressDialog prDialog;
    ActivityResultLauncher actResLauncher;

    String teamId;
    String teamName;
    String projectTitle;
    String projectDesc;
    String projectTotalStory;
    String memberName1;
    String memberName2;
    String memberName3;
    String memberName4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize() {
        edTeamId = findViewById(R.id.edTeamId);
        edTeamName = findViewById(R.id.edTeamName);
        edProjectTitle = findViewById(R.id.edProjectTitle);
        edProjectDesc = findViewById(R.id.edProjectDesc);
        edProjectTotalStory = findViewById(R.id.edProjectTotalStory);
        edMemberName1 = findViewById(R.id.edMemberName1);
        edMemberName2 = findViewById(R.id.edMemberName2);
        edMemberName3 = findViewById(R.id.edMemberName3);
        edMemberName4 = findViewById(R.id.edMemberName4);

        tvSelectPhoto = findViewById(R.id.tvSelectPhoto);
        imPhoto = findViewById(R.id.imPhoto);

        btnAdd = findViewById(R.id.btnAdd);
        btnFind = findViewById(R.id.btnFind);
        btnList = findViewById(R.id.btnList);
        btnQuit = findViewById(R.id.btnQuit);

        btnAdd.setOnClickListener(this);
        btnFind.setOnClickListener(this);
        btnList.setOnClickListener(this);
        btnQuit.setOnClickListener(this);
        imPhoto.setOnClickListener(this);


        teamsDb = FirebaseDatabase.getInstance().getReference("Teams");
        storageRef = FirebaseStorage.getInstance().getReference();
        actResLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> getPhoto(result));


    }


    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.imPhoto:
                selectPhoto();
                break;
            case R.id.btnAdd:
                addTeam();
                break;
            case R.id.btnFind:
                findTeam();
                break;
            case R.id.btnList:
                listTeams();
                break;
            case R.id.btnQuit:
                System.exit(0);
                break;
        }
    }
    private void clearWidgets(){
        edTeamId.setText(null);
        edTeamName.setText(null);
        edProjectTitle.setText(null);
        edProjectTotalStory.setText(null);
        edProjectDesc.setText(null);
        edMemberName1.setText(null);
        edMemberName2.setText(null);
        edMemberName3.setText(null);
        edMemberName4.setText(null);
        imPhoto.setImageResource(R.drawable.noimage);
        tvSelectPhoto.setVisibility(View.VISIBLE);
    }

    private void selectPhoto() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        actResLauncher.launch(Intent.createChooser(i, "Select Photo"));
    }

    private void getPhoto(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && (result.getData() != null ? result.getData().getData() : null) != null) {

            try {
                photoPath = result.getData().getData();
                Bitmap photo = MediaStore.Images.Media.getBitmap(getContentResolver(), photoPath);
                imPhoto.setImageBitmap(photo);
//                imPhoto.setImageURI(photoPath);


                isPhotoChosen = true;
                tvSelectPhoto.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Successfully Select Photo!", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Fail in Select Photo!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addTeam() {

        teamId = edTeamId.getText().toString();
        teamName = edTeamName.getText().toString();
        projectTitle = edProjectTitle.getText().toString();
        projectDesc = edProjectDesc.getText().toString();
        projectTotalStory = edProjectTotalStory.getText().toString();
        memberName1 = edMemberName1.getText().toString();
        memberName2 = edMemberName2.getText().toString();
        memberName3 = edMemberName3.getText().toString();
        memberName4 = edMemberName4.getText().toString();

        //validate input
        if (TextUtils.isEmpty(teamId) || TextUtils.isEmpty(teamName) || TextUtils.isEmpty(projectTitle) || TextUtils.isEmpty(projectDesc) || TextUtils.isEmpty(projectTotalStory) || TextUtils.isEmpty(memberName4) || TextUtils.isEmpty(memberName3) || TextUtils.isEmpty(memberName2) || TextUtils.isEmpty(memberName1) || !isPhotoChosen) {
            Toast.makeText(this, "Plz fill in all team fields and select a team photo", Toast.LENGTH_SHORT).show();
        } else {
            try {
                getUploadedPhotoUrl();
//                Project project = new Project(projectTitle,projectDesc,Integer.valueOf(projectTotalStory));
//                ArrayList<String> members = new ArrayList<>( Arrays.asList(memberName1,memberName2,memberName3,memberName4));
////                ArrayList<String> members = new ArrayList<String>(List.of(memberName1,memberName2,memberName3,memberName4) );
//                Team team = new Team(Integer.valueOf(teamId),teamName,photoUrl,project,members);
//                teamsDb.child(teamId).setValue(team);
//                Toast.makeText(this, "Team "+teamName+" successfully added!", Toast.LENGTH_SHORT).show();
//                clearWidgets();

            } catch (Exception ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getUploadedPhotoUrl() {
//        if (photoPath!=null){//not necessary,isChosenPhoto is true, always have a photo
        prDialog = new ProgressDialog(this);
        prDialog.setTitle("Uploading photo in progress ...");
        prDialog.show();
        //upload the photo
        String photoName = "TeamAsia.png";
        photoRef = storageRef.child("images/" + photoName);
        photoRef.putFile(photoPath).addOnSuccessListener(this);
        photoRef.putFile(photoPath).addOnFailureListener(this);


    }

    @Override
    public void onFailure(@NonNull Exception e) {
        prDialog.dismiss();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(Object o) {
        prDialog.dismiss();
        Toast.makeText(this, "Success upload team photo!", Toast.LENGTH_SHORT).show();
        photoRef.getDownloadUrl().addOnCompleteListener(this);

    }
    @Override
    public void onComplete(@NonNull Task task) {
        photoUrl=task.getResult().toString();
        Log.d("Teams_FIREBASE",photoUrl);


        Project project = new Project(projectTitle, projectDesc, Integer.valueOf(projectTotalStory));
        ArrayList<String> members = new ArrayList<>(Arrays.asList(memberName1, memberName2, memberName3, memberName4));
//                ArrayList<String> members = new ArrayList<String>(List.of(memberName1,memberName2,memberName3,memberName4) );
        Team team = new Team(Integer.valueOf(teamId), teamName, photoUrl, project, members);
        teamsDb.child(teamId).setValue(team);
        Toast.makeText(this, "Team " + teamName + " successfully added!", Toast.LENGTH_SHORT).show();
        clearWidgets();


    }

    private void findTeam() {
        teamId=edTeamId.getText().toString();
        if (TextUtils.isEmpty(teamId)){
            Toast.makeText(this, "team Id cannot be empty to find!", Toast.LENGTH_SHORT).show();
        }else{
            teamsDb.child(teamId).addValueEventListener(this);
        }
    }



    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        try {
            if (snapshot.exists()){
                Team team = snapshot.getValue(Team.class);
                edTeamName.setText(team.getName());
                photoUrl=team.getPhoto();
                Picasso.with(this).load(photoUrl).placeholder(R.drawable.temp_image).into(imPhoto);
                tvSelectPhoto.setVisibility(View.INVISIBLE);
                Project prj = team.getProject();
                edProjectTitle.setText(prj.getTitle());
                edProjectDesc.setText(prj.getDescription());
                edProjectTotalStory.setText(prj.getTotal()+"");
                ArrayList<String>members=team.getMembers();
                edMemberName1.setText(members.get(0));
                edMemberName2.setText(members.get(1));
                edMemberName3.setText(members.get(2));
                edMemberName4.setText(members.get(3));
                Toast.makeText(this, "Success found team with id"+teamId, Toast.LENGTH_SHORT).show();




            }else{
                Toast.makeText(this, "Team with id "+teamId+" not exist!", Toast.LENGTH_SHORT).show();
                clearWidgets();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }
    private void listTeams() {
        teamsDb.addChildEventListener(this);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        Team team = snapshot.getValue(Team.class);
        Log.d("Teams_FIREBASE",team.toString());
        Map project = (Map)snapshot.child("project").getValue();
//        Log.d("Teams_FIREBASE_Project",project.toString());
        Log.d("Teams_FIREBASE_Project",project.get("title").toString());
        Log.d("Teams_FIREBASE_Project",project.get("description").toString());
        Log.d("Teams_FIREBASE_Project",project.get("total").toString());
        ArrayList<String> members = (ArrayList<String>)snapshot.child("members").getValue();
        for (String member:members) {
            Log.d("Teams_FIREBASE_Members",member);
        }
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }


}