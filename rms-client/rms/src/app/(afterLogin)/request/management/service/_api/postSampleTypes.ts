import {Query} from "@/model/Query";


export async function postSampleTypes(search: Query) {
    return await fetch(`/w-api/management-service/sample-types`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}