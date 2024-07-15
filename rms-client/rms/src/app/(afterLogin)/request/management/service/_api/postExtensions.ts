export async function postExtension(extension: {
    extension_id: any;
    service_id: string | undefined;
    required: undefined | boolean
}) {
    return await fetch(`/w-api/management-service/services/${extension.service_id}/extensions/${extension.extension_id}`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(extension),
        credentials: 'include',
        cache: 'no-store'
    });
}