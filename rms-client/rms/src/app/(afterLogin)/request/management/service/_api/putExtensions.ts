export async function putExtensions(extension: {
    extension_id: any;
    service_id: string | undefined;
    required: undefined | boolean
}) {
    return await fetch(`/w-api/management-service/services/${extension.service_id}/extensions/${extension.extension_id}`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(extension),
        credentials: 'include',
        cache: 'no-store'
    });
}