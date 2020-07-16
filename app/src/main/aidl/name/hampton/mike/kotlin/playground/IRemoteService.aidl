// IRemoteService.aidl
package name.hampton.mike.kotlin.playground;

// Declare any non-default types here with import statements
import name.hampton.mike.kotlin.playground.IRemoteServiceCallback;

/** Example service interface */
interface IRemoteService {
    /**
     * Often you want to allow a service to call back to its clients.
     * This shows how to do so, by registering a callback interface with
     * the service.
     */
    void registerCallback(IRemoteServiceCallback cb);

    /**
     * Remove a previously registered callback interface.
     */
    void unregisterCallback(IRemoteServiceCallback cb);
}