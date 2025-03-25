import {Query} from "@/model/Query";

export async function searchRequests(search: Query, module: string) {
    return await fetch(`/w-api/${module}-service/requests/search`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}