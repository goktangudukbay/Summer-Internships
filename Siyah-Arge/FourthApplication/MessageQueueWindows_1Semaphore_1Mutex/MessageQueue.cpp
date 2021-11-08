/*
Message Queue Class
Mustafa Goktan Gudukbay
*/

#include "MessageQueue.h"

MessageQueue::MessageQueue(){
    notOpened = true;
}

MessageQueue::~MessageQueue(){}

bool MessageQueue::mq_open(TCHAR mappingObjectName[], DWORD maxNoOfMessages, PermissionType permission){

    this->permission = permission;

    int maxSize = maxNoOfMessages * sizeof(struct Message) + 4*sizeof(DWORD);//rear and front of the queue

    TCHAR szName[200] = TEXT("Global\\");

   
     _tcscat_s(szName, 200, mappingObjectName);


    hMapFile = CreateFileMapping(
                 INVALID_HANDLE_VALUE,    // use paging file
                 NULL,                    // default security
                 PAGE_READWRITE,          // read/write access
                 0,                       // maximum object size (high-order DWORD)
                 maxSize,                // maximum object size (low-order DWORD)
                 szName);     // name of mapping object

    if (hMapFile == NULL)
   {
      _tprintf(TEXT("Could not create file mapping object (%d).\n"),
             GetLastError());
      return false;
   }

    DWORD errorCode = GetLastError();

   if((permission == read) && (errorCode == ERROR_ALREADY_EXISTS)){//already a reader created the message queue.
       CloseHandle(hMapFile);
       return false;
   }

   if((permission == write) && (errorCode == ERROR_SUCCESS)){//a writer cannot create the message queue.
       CloseHandle(hMapFile);
       return false;
   }

   pBuf = (LPTSTR) MapViewOfFile(hMapFile, FILE_MAP_ALL_ACCESS, // read/write permission
                        0,
                        0,
                        maxSize);

   if (pBuf == NULL)
   {
      _tprintf(TEXT("Could not map view of file (%d).\n"),
             GetLastError());
       CloseHandle(hMapFile);
      return false;
   }

    TCHAR fullName[200] = TEXT("Global\\Full");

     _tcscat_s(fullName, 200, mappingObjectName);

    full = CreateSemaphore( 
        NULL,             // default security attributes
        0,                // initial count
        maxNoOfMessages,  // maximum count
        fullName);

    if (full == NULL){ 
       CloseHandle(hMapFile);
        _tprintf(TEXT("Semaphore Error (%d).\n"),
             GetLastError());
        CloseHandle(full);
        return false;
    }

    
    TCHAR mutexName[200] = TEXT("Global\\Mutex");

     _tcscat_s(mutexName, 200, mappingObjectName);


    mutex = CreateMutex( 
        NULL,                   // default security attributes
        FALSE,                  // initially not owned
        mutexName);    

    if (mutex == NULL){ 
       CloseHandle(hMapFile);
        _tprintf(TEXT("Mutex Error (%d).\n"),
             GetLastError());
        CloseHandle(mutex);
        CloseHandle(full);
        return false;
    }

    if(permission == write)
        return true;

    CopyMemory((PVOID)pBuf, &maxNoOfMessages, sizeof(DWORD));//max number of messages

    DWORD size = 0;
    CopyMemory((PVOID)((PCHAR)pBuf + 1*sizeof(DWORD)), &size, sizeof(DWORD));//size

    DWORD front = 0;
    CopyMemory((PVOID)((PCHAR)pBuf + 2*sizeof(DWORD)), &front, sizeof(DWORD));//front

    int rear = -1;
    CopyMemory((PVOID)((PCHAR)pBuf + 3*sizeof(DWORD)), &rear, sizeof(int));//rear

    notOpened = false;

    return true;
}

bool MessageQueue::mq_send(struct Message* message){

    if(this->permission == read)
        return false;

    DWORD front, size, maxNoOfMessages;
    int rear;

    WaitForSingleObject(mutex, INFINITE);

    CopyMemory((PVOID)(&maxNoOfMessages), (PVOID)pBuf, sizeof(DWORD));//maxNoOfMessages
    CopyMemory((PVOID)(&size), (PVOID)((PCHAR)pBuf + 1*sizeof(DWORD)), sizeof(DWORD));//size

    if(size == maxNoOfMessages)
        return false;

    CopyMemory((PVOID)(&front), (PVOID)((PCHAR)pBuf + 2*sizeof(DWORD)), sizeof(DWORD));//front
    CopyMemory((PVOID)(&rear), (PVOID)((PCHAR)pBuf + 3*sizeof(DWORD)), sizeof(int));//rear

    int indexToInsert = (rear + 1) % maxNoOfMessages;
    CopyMemory((PVOID)((PCHAR)pBuf + 4*sizeof(DWORD) + indexToInsert*MESSAGE_SIZE), message, sizeof(*message));

    //write rear and new size into the memory into into the shared memory
    size++;

    rear++;
    CopyMemory((PVOID)((PCHAR)pBuf + 1*sizeof(DWORD)), &size, sizeof(DWORD));//size
    CopyMemory((PVOID)((PCHAR)pBuf + 3*sizeof(DWORD)), &rear, sizeof(int));//rear
    
    ReleaseMutex(mutex);
    ReleaseSemaphore(full, 1, NULL);

    return true;
}

bool MessageQueue::mq_receive(struct Message* message){

    if(this->permission == write)
        return false;

    DWORD size;

    WaitForSingleObject(full, INFINITE);
    WaitForSingleObject(mutex, INFINITE);

    CopyMemory((PVOID)(&size), (PVOID)((PCHAR)pBuf + 1*sizeof(DWORD)), sizeof(DWORD));//get size
            
    DWORD front, maxNoOfMessages;
    CopyMemory((PVOID)(&front), (PVOID)((PCHAR)pBuf + 2*sizeof(DWORD)), sizeof(DWORD));//get front
    CopyMemory((PVOID)(&maxNoOfMessages), (PVOID)pBuf, sizeof(DWORD));//get maxNoOfMessages

    int indexToDelete = front % maxNoOfMessages;

    CopyMemory((PVOID)(message->buffer), (PVOID)((PCHAR)pBuf + 4*sizeof(DWORD) + indexToDelete*MESSAGE_SIZE), sizeof(*message));//front

   //write rear and new size into the memory into into the shared memory

    size--;

    front++;
    CopyMemory((PVOID)((PCHAR)pBuf + 1*sizeof(DWORD)), &size, sizeof(DWORD));//size
    CopyMemory((PVOID)((PCHAR)pBuf + 2*sizeof(DWORD)), &front, sizeof(DWORD));//front

    ReleaseMutex(mutex);

    return true;
}

bool MessageQueue::mq_close(){
    if(notOpened)
        return false;

    if(permission == write){
        return false;
    }

    CloseHandle(hMapFile);
    CloseHandle(hMapFile);
    CloseHandle(full);

    return true;
}