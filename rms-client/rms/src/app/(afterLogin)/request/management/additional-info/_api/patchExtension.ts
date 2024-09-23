import {Extension} from "@/model/Extension";

export async function patchExtension(extension: Extension) {
    return await fetch(`/w-api/management-service/extensions/${extension.id}`, {
        method: 'PATCH',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            type: extension.type,
            regex: extension.regex
        }),
        credentials: 'include',
        cache: 'no-store'
    });
}