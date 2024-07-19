import {Organization} from "@/model/Organization";

export async function PatchOrganization(organization: Organization) {
    return await fetch(`/w-api/organization-service/organizations/${organization.id}`, {
        method: 'PATCH',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(organization),
        credentials: 'include',
        cache: 'no-store'
    });
}