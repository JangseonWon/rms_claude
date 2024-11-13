import {Extension} from "@/model/Extension";

export async function patchExtension(extension: Extension) {
    return await fetch(`/w-api/management-service/extensions/${extension.id}`, {
        method: 'PATCH',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(extension),
        credentials: 'include',
        cache: 'no-store'
    });
}