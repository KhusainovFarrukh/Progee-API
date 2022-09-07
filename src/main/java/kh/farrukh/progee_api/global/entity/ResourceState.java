package kh.farrukh.progee_api.global.entity;

/**
 * Enum class for publish states of resource (Language, Framework and etc.)
 *
 * WAITING - User created the resource, but admins not accepted yet
 * APPROVED - Admins accepted or resource itself was created by admins
 * DECLINED - Admins declined resource that user had created
 */
public enum ResourceState {
    WAITING,
    APPROVED,
    DECLINED
}