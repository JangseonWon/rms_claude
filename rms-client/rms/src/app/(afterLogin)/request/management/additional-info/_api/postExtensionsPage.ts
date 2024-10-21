import {Query} from "@/model/Query";

export async function postExtensionsPage(search: Query) {
    return await fetch(`/w-api/management-service/extensions`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}