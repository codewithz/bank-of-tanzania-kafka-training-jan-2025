import random

def generate_review(improvement, jira_management, team_bonding, partially_completed, signed_off, special_notes):
    

    
    improvement_statements = [
        f"Compared to yesterday, the team has shown {improvement}.", 
        f"Noteworthy improvement from yesterday: {improvement}. Keep it up!", 
        f"Yesterday to today, there's visible growth: {improvement}."
    ]
    
    jira_statements = [
        f"JIRA board is looking {jira_management}.", 
        f"Task management on JIRA seems {jira_management}, which is great to see!",
        f"JIRA updates are {jira_management}, helping in better tracking."
    ]
    
    bonding_statements = [
        f"Team bonding is {team_bonding}, which is crucial for collaboration.",
        f"Great to see {team_bonding} levels of team bonding!",
        f"A well-bonded team performs better, and {team_bonding} bonding is a positive sign."
    ]
    
    stories_statements = [
        f"{partially_completed} stories are partially completed and {signed_off} stories have been signed off today. Good progress!",
        f"Progress made: {partially_completed} partially completed stories and {signed_off} signed-off stories. Good job!",
        f"With {partially_completed} stories nearing completion and {signed_off} signed-off stories, the sprint is shaping up well!"
    ]
    
    special_statements = [
        f"Special shoutout for: {special_notes}.", 
        f"A noteworthy moment today: {special_notes}.", 
        f"Something remarkable happened today: {special_notes}."
    ] if special_notes else [""]
    
 
    
    review = "\n".join([
       
        random.choice(improvement_statements),
        random.choice(jira_statements),
        random.choice(bonding_statements),
        random.choice(stories_statements),
        random.choice(special_statements),
       
    ])
    
    return review.strip()

if __name__ == "__main__":
    improvement = input("How has the team improved from yesterday? ")
    jira_management = input("How is the JIRA board management? ")
    team_bonding = input("How is the team bonding? ")
    partially_completed = input("Number of stories partially completed? ")
    signed_off = input("Number of stories signed off? ")
    special_notes = input("Anything special to highlight? (Optional) ")
    
    print("\n--- Team Review ---\n")
    print(generate_review(improvement, jira_management, team_bonding, partially_completed, signed_off, special_notes))
