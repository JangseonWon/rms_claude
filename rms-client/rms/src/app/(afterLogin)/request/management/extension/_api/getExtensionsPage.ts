import {Paging} from "@/model/Paging";

export async function getExtensionsPage(search: Paging) {
    return await fetch(`/w-api/management-service/extension-page`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}