package kh.farrukh.progee_api.utils.exception;

public class ResourceDuplicateNameException extends IllegalStateException {

    public ResourceDuplicateNameException(Object resource, String name) {
        super(resource.getClass().getName() + " with name " + name + " already exists");
    }

    public ResourceDuplicateNameException(String resourceName, String name) {
        super(resourceName + " with name " + name + " already exists");
    }
}