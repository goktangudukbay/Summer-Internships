#include <windows.h>
#include <tchar.h>
#include <strsafe.h>
#include "MessageQueue.h"
#include <time.h>


#define MAX_THREADS 5
TCHAR mappingObjectName[]=TEXT("MyFileMappingObject");
DWORD maxNoOfMessages = 5;

void rand_string(char *str)
{
    /*srand(time(0));

    const char charset[] = "abcdefghijklmnopqrstuvwxyz0123456789";
    for (int n = 0; n < MESSAGE_SIZE; n++) {
        int key = rand() % (int) (sizeof charset - 1);
        str[n] = charset[key];
    }
    str[MESSAGE_SIZE] = '\0';*/

    sprintf(str,"%d", GetCurrentThreadId());
}

DWORD WINAPI writerThread( LPVOID lpParam ) 
{ 
    Sleep(10000);
    MessageQueue mq;
    if(!(mq.mq_open(mappingObjectName, maxNoOfMessages, write))){
        printf("Message queue could not be opened.");
        return 1;
    }
        
    struct Message* m = new Message;
    memset(m->buffer, 0, MESSAGE_SIZE);

    while(TRUE){
        rand_string(m->buffer);
        if(!mq.mq_send(m)){
            printf("\nMessage was not sent.\n");
        }
        Sleep(5000);
    }
    mq.mq_close();
    return 0; 
}

DWORD WINAPI readerThread( LPVOID lpParam ) 
{ 
    MessageQueue mq;
    if(!(mq.mq_open(mappingObjectName, maxNoOfMessages, read))){
        printf("Message queue could not be opened.");
        return 1;
    }

    struct Message* m = new Message;
    
    while(TRUE){
        if(mq.mq_receive(m)){
            printf("\nMessage was received.\n");
            printf("%s\n\n", m->buffer);
        }
        else{
            printf("Message was not received.");
        }
        Sleep(5000);


    }
    mq.mq_close();
    return 0; 
}

int _tmain()
{

    HANDLE  hThreadArray[MAX_THREADS]; 
    DWORD   dwThreadIdArray[MAX_THREADS];

    hThreadArray[0] = CreateThread(NULL, 0, readerThread, NULL, 0, &dwThreadIdArray[0]);

    if (hThreadArray[0] == NULL) 
    {
        printf("Create thread failed.\n");
        ExitProcess(3);
    }   

    for(int i = 1; i < MAX_THREADS; i++){
        hThreadArray[i] = CreateThread(NULL, 0, writerThread, NULL, 0, &dwThreadIdArray[i]);

        if (hThreadArray[i] == NULL) 
        {
           printf("Create thread failed.\n");
           ExitProcess(3);
        }   
    }  

    WaitForMultipleObjects(MAX_THREADS, hThreadArray, TRUE, INFINITE);
 

    for(int i=0; i < MAX_THREADS; i++)
        CloseHandle(hThreadArray[i]);

    return 0;
}
