import {Categories} from "@/model/Categories";

export async function putCategories(category: Categories) {
    return await fetch(`/w-api/management-service/categories`, {
        method: 'PUT',
        credentials: 'include',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(category),
        cache: 'no-store'
    });
}