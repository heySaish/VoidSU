#include <linux/kobject.h>
#include <linux/string.h>
#include <linux/sysfs.h>
#include <linux/init.h>
#include "void_version.h"

#ifndef VOID_KERNEL_VERSION
#define VOID_KERNEL_VERSION "1.0.0"
#endif

static struct kobject *void_kobj;

static ssize_t version_show(struct kobject *kobj, struct kobj_attribute *attr, char *buf)
{
    return sprintf(buf, "%s\n", VOID_KERNEL_VERSION);
}

static struct kobj_attribute version_attribute =
    __ATTR(version, 0444, version_show, NULL);

void void_version_init(void)
{
    int ret;
    void_kobj = kobject_create_and_add("void_kernel", kernel_kobj);
    if (!void_kobj)
        return;

    ret = sysfs_create_file(void_kobj, &version_attribute.attr);
    if (ret) {
        kobject_put(void_kobj);
        void_kobj = NULL;
    }
}

void void_version_exit(void)
{
    if (void_kobj) {
        kobject_put(void_kobj);
        void_kobj = NULL;
    }
}
