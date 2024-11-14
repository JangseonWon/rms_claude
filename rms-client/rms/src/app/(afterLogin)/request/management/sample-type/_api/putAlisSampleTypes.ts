import {Query} from "@/model/Query";

export async function putAlisSampleTypes(search: Query) {
    return await fetch(`/w-api/management-service/alis/sample-types`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}