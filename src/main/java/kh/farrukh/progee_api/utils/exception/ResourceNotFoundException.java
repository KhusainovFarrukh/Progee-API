package kh.farrukh.progee_api.utils.exception;

public class ResourceNotFoundException extends IllegalStateException {

    public ResourceNotFoundException(Object resource, long resourceId) {
        super(resource.getClass().getName() + " with id " + resourceId + " does not exist");
    }

    public ResourceNotFoundException(String resourceName, long resourceId) {
        super(resourceName + " with id " + resourceId + " does not exist");
    }
}
