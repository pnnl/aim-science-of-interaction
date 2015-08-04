package gov.pnnl.aim.biz;

public class BusinessConfig {

	public static final String ASI_USER = "test_user";
	public static final String ASI_PWD  = "test_password";

	public static final String ASI_OPA_TOPIC  = "aim-piers-opa";
	public static final String ASI_OPA_MODEL_TOPIC = "aim-piers-opa-model";
	public static final String ASI_OPA_PROP_TOPIC  = "aim-piers-opa-proportions";
	
	public static final String ASI_SHYRE_TOPIC  = "aim-piers-shyre";
	public static final String ASI_POP_TOPIC  = "aim-piers-pop";

	public static final String ASI_OPA_FEEDBACK_TOPIC  = "aim-piers-soi-opa-feedback";
	
	public static final String ASI_NOUS_pathQuestionTopicName = "nous-path-question";
	public static final String ASI_NOUS_pathAnswerTopicName = "nous-path-answer";
	public static final String ASI_NOUS_profileQuestionTopicName  = "nous-profile-question";
	public static final String ASI_NOUS_profileAnswerTopicName  = "nous-profile-answer";
	public static final String ASI_NOUS_STREAM  = "aim-shyre-nous-soi";
	public static final int ASI_NOUS_RETRIEVE_ATTEMPTS = 3;

	public static final String PIERS_DB_URL = "http://172.18.5.3:9080/piers/rest/piersRecord/";

}
