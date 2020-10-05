package com.example.firstapplication.service;

class URLs {
    static final String CLOUD_MESSAGE_API = "https://fcm.googleapis.com/fcm/send";
    static final String BASE_MESSIER_API = "https://laboratory.binus.ac.id/lapi/";
    static final String ACADEMIC_API = "http://academic.slc/api/training/";

    static final String AUTH_MESSIER = BASE_MESSIER_API + "API/Account/LogOn";
    static final String USER_IDENTIFY_MESSIER = BASE_MESSIER_API + "API/Account/Me";

    static final String TEACHING_API = BASE_MESSIER_API + "API/Lecturer/GetTeachingSchedule";
    static final String JOBS_API = BASE_MESSIER_API + "API/Schedule/GetJobsAssistant";
    static final String SEMESTER_API = BASE_MESSIER_API + "API/Schedule/GetSemesters";


    static final String TRAINING_API = ACADEMIC_API + "getTraining/da39a3ee5e6b4b0d3255bfef95601890afd80709/";

}
