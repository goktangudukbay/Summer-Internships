/*
Message Queue Class Header File
Mustafa Goktan Gudukbay
*/

#ifndef __MESSAGE_QUEUE_H
#define __MESSAGE_QUEUE_H

#define MESSAGE_SIZE 256   


#include <windows.h>
#include <string.h>
#include <tchar.h>
#include <cstdio>


struct Message {
  char buffer[MESSAGE_SIZE];
};

enum PermissionType {read, write, read_write};

class MessageQueue{
public:
    MessageQueue();
    ~MessageQueue();
    bool mq_open(TCHAR mappingObjectName[], DWORD maxNoOfMessages, PermissionType permission);
    bool mq_send(struct Message* message);
    bool mq_receive(struct Message* message);
    bool mq_close();

private:
    bool notOpened;
    HANDLE hMapFile;
    LPCTSTR pBuf;
    PermissionType permission; 
    HANDLE empty;
    HANDLE mutex;
    HANDLE full;
};
#endif
