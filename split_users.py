import csv, os, sys

out_dir = "users"
if not os.path.exists(out_dir):
    os.makedirs(out_dir)

with open(sys.argv[1], 'r') as csv_file:
    csv_reader = csv.reader(csv_file)

    out_file = ""
    current_user = ""

    header = csv_file.readline().replace(",USER_ID", "")

    for row in csv_reader:
        if not current_user:
            current_user = row[-2]
            print("user: " + current_user)
            out_file = open(os.path.join(out_dir, "user_" + current_user + ".csv"), "w")
            out_file.write(header)
        
        if current_user in row[-2]:
            del row[-2]
            out_file.write(",".join([str(x) for x in row]) + '\n')
        else:
            out_file.close()
            current_user = row[-2]
            print("user: " + current_user)
            out_file = open(os.path.join(out_dir, "user_" + current_user.replace("?", "") + ".csv"), "w")
            out_file.write(header)
            del row[-2]
            out_file.write(",".join([str(x) for x in row]) + '\n')
